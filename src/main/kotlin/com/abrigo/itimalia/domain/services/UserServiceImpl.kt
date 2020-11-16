package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.user.NewUserRequest
import com.abrigo.itimalia.domain.entities.user.User
import com.abrigo.itimalia.domain.entities.user.UserLoginRequest
import com.abrigo.itimalia.domain.entities.user.UserRequest
import com.abrigo.itimalia.domain.entities.user.UserRole
import com.abrigo.itimalia.domain.entities.user.toNewUser
import com.abrigo.itimalia.domain.entities.user.toUser
import com.abrigo.itimalia.domain.entities.user.toUserLogin
import com.abrigo.itimalia.domain.exceptions.EmailAlreadyExistsException
import com.abrigo.itimalia.domain.exceptions.UnauthorizedDifferentUserChangeException
import com.abrigo.itimalia.domain.exceptions.UnauthorizedRoleChangeException
import com.abrigo.itimalia.domain.exceptions.UnmodifiedUserException
import com.abrigo.itimalia.domain.exceptions.UserNotFoundException
import com.abrigo.itimalia.domain.jwt.JWTService
import com.abrigo.itimalia.domain.repositories.UserRepository
import com.abrigo.itimalia.domain.validation.Validator
import org.joda.time.DateTime

class UserServiceImpl(
    private val userRepository: UserRepository,
    private val jwtService: JWTService,
    private val validatorNewUser: Validator<NewUserRequest>,
    private val validatorUser: Validator<UserRequest>,
    private val validatorUserLogin: Validator<UserLoginRequest>
) : UserService {

    override fun getIdByToken(token: String): Int {
        return userRepository.getIdByToken(token)
    }

    override fun update(id: Int, userRequest: UserRequest, role: UserRole, email: String): User {
        validatorUser.validate(userRequest)
        val userToBeModified = userRepository.get(id)
        val userDTO = userRequest.toUser()
        try {
            val user = userRepository.findByEmail(userDTO.email)
            //if found an email in another user, cannot update the email to other that exists
            if (user.id != id) {
                throw EmailAlreadyExistsException()
            } else {
                throw UserNotFoundException()
            }
        } catch (exception: UserNotFoundException) {
            val newUserDTO = User(
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

            if (role == UserRole.ADMIN) {
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

    override fun login(userLoginRequest: UserLoginRequest): User {
        validatorUserLogin.validate(userLoginRequest)
        val newUserLogin = userLoginRequest.toUserLogin()
        val user = userRepository.findByCredentials(newUserLogin.email, newUserLogin.password)
        val token = jwtService.sign(user.email, user.role)
        val loggedUser = user.copy(token = token)
        userRepository.update(loggedUser.id!!, loggedUser)
        return userRepository.get(loggedUser.id)
    }

    override fun add(newUserRequest: NewUserRequest): User {
        validatorNewUser.validate(newUserRequest)
        val newUser = newUserRequest.toNewUser()
        try {
            userRepository.findByEmail(newUser.email)
            throw EmailAlreadyExistsException()
        } catch (exception: UserNotFoundException) {
            val actualDate = DateTime.now()
            val newUserDTO = User(
                null,
                newUser.email,
                newUser.password,
                newUser.birthDate,
                newUser.gender,
                newUser.name,
                newUser.phone,
                UserRole.USER,
                actualDate,
                actualDate,
                jwtService.sign(newUser.email, UserRole.USER)
            )
            return userRepository.add(newUserDTO)
        }

    }

    override fun delete(id: Int, role: UserRole, email: String) {
        val userToBeDeleted = userRepository.get(id)

        if (role == UserRole.ADMIN) {
            return userRepository.delete(id)
        }
        if (userToBeDeleted.email == email) {
            return userRepository.delete(id)
        }
        throw UnauthorizedDifferentUserChangeException()
    }

    override fun get(id: Int): User {
        return userRepository.get(id)
    }

    private fun updateCall(newUser: User, userToBeModified: User, id: Int): User {
        if (newUser == userToBeModified) {
            throw UnmodifiedUserException()
        }

        val actualDate = DateTime.now()
        val modifiedUserDTO = User(
            userToBeModified.id,
            newUser.email,
            newUser.password,
            newUser.birthDate,
            newUser.gender,
            newUser.name,
            newUser.phone,
            newUser.role,
            userToBeModified.creationDate,
            actualDate,
            jwtService.sign(newUser.email, newUser.role)
        )
        return userRepository.update(id, modifiedUserDTO)
    }

}