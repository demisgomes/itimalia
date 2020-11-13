package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.user.NewUserRequest
import com.abrigo.itimalia.domain.entities.user.Roles
import com.abrigo.itimalia.domain.entities.user.UserDTO
import com.abrigo.itimalia.domain.entities.user.UserDTORequest
import com.abrigo.itimalia.domain.entities.user.UserLoginRequest
import com.abrigo.itimalia.domain.entities.user.toNewUser
import com.abrigo.itimalia.domain.entities.user.toUserDTO
import com.abrigo.itimalia.domain.entities.user.toUserLogin
import com.abrigo.itimalia.domain.exceptions.EmailAlreadyExistsException
import com.abrigo.itimalia.domain.exceptions.UnauthorizedDifferentUserChangeException
import com.abrigo.itimalia.domain.exceptions.UnauthorizedRoleChangeException
import com.abrigo.itimalia.domain.exceptions.UnmodifiedUserException
import com.abrigo.itimalia.domain.exceptions.UserNotFoundException
import com.abrigo.itimalia.domain.jwt.JWTService
import com.abrigo.itimalia.domain.repositories.UserRepository
import com.abrigo.itimalia.domain.validation.Validator
import io.javalin.core.security.Role
import org.joda.time.DateTime

class UserServiceImpl(
    private val userRepository: UserRepository,
    private val jwtService: JWTService,
    private val validatorNewUser: Validator<NewUserRequest>,
    private val validatorUserDTO: Validator<UserDTORequest>,
    private val validatorUserLogin: Validator<UserLoginRequest>
) : UserService {

    override fun getIdByToken(token: String): Int {
        return userRepository.getIdByToken(token)
    }

    override fun update(id: Int, userDTORequest: UserDTORequest, role: Role, email: String): UserDTO {
        validatorUserDTO.validate(userDTORequest)
        val userToBeModified = userRepository.get(id)
        val userDTO = userDTORequest.toUserDTO()
        try {
            val user = userRepository.findByEmail(userDTO.email)
            //if found an email in another user, cannot update the email to other that exists
            if (user.id != id) {
                throw EmailAlreadyExistsException()
            } else {
                throw UserNotFoundException()
            }
        } catch (exception: UserNotFoundException) {
            val newUserDTO = UserDTO(
                userToBeModified.id,
                userDTO.email,
                userDTO.password,
                userDTO.birthDate,
                userDTO.gender,
                userDTO.name,
                userDTO.phone,
                userDTO.role,
                userToBeModified.creationDate,
                userToBeModified.modificationDate,
                userToBeModified.token
            )

            if (role == Roles.ADMIN) {
                return updateCall(newUserDTO, userToBeModified, id)
            }

            if (userToBeModified.email == email) {
                if (userToBeModified.role == newUserDTO.role) {
                    return updateCall(newUserDTO, userToBeModified, id)
                }
                throw UnauthorizedRoleChangeException()
            }
            throw UnauthorizedDifferentUserChangeException()
        }

    }

    override fun login(userLoginRequest: UserLoginRequest): UserDTO {
        validatorUserLogin.validate(userLoginRequest)
        val newUserLogin = userLoginRequest.toUserLogin()
        val user = userRepository.findByCredentials(newUserLogin.email, newUserLogin.password)
        val token = jwtService.sign(user.email, user.role)
        val loggedUser = user.copy(token = token)
        userRepository.update(loggedUser.id!!, loggedUser)
        return userRepository.get(loggedUser.id!!)
    }

    override fun add(newUserRequest: NewUserRequest): UserDTO {
        validatorNewUser.validate(newUserRequest)
        val newUser = newUserRequest.toNewUser()
        try {
            userRepository.findByEmail(newUser.email)
            throw EmailAlreadyExistsException()
        } catch (exception: UserNotFoundException) {
            val actualDate = DateTime.now()
            val newUserDTO = UserDTO(
                null,
                newUser.email,
                newUser.password,
                newUser.birthDate,
                newUser.gender,
                newUser.name,
                newUser.phone,
                Roles.USER,
                actualDate,
                actualDate,
                jwtService.sign(newUser.email, Roles.USER)
            )
            return userRepository.add(newUserDTO)
        }

    }

    override fun delete(id: Int, role: Role, email: String) {
        val userToBeDeleted = userRepository.get(id)

        if (role == Roles.ADMIN) {
            return userRepository.delete(id)
        }
        if (userToBeDeleted.email == email) {
            return userRepository.delete(id)
        }
        throw UnauthorizedDifferentUserChangeException()
    }

    override fun get(id: Int): UserDTO {
        return userRepository.get(id)
    }

    private fun updateCall(newUserDTO: UserDTO, userToBeModified: UserDTO, id: Int): UserDTO {
        if (newUserDTO == userToBeModified) {
            throw UnmodifiedUserException()
        }

        val actualDate = DateTime.now()
        val modifiedUserDTO = UserDTO(
            userToBeModified.id,
            newUserDTO.email,
            newUserDTO.password,
            newUserDTO.birthDate,
            newUserDTO.gender,
            newUserDTO.name,
            newUserDTO.phone,
            newUserDTO.role,
            userToBeModified.creationDate,
            actualDate,
            jwtService.sign(newUserDTO.email, newUserDTO.role)
        )
        return userRepository.update(id, modifiedUserDTO)
    }

}