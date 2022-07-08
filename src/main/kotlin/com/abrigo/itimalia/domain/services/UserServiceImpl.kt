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
import com.abrigo.itimalia.domain.exceptions.UserNotFoundException
import com.abrigo.itimalia.domain.jwt.JWTService
import com.abrigo.itimalia.domain.repositories.UserRepository
import com.abrigo.itimalia.domain.validation.ValidatorRequest
import org.joda.time.DateTime

class UserServiceImpl(
    private val userRepository: UserRepository,
    private val jwtService: JWTService,
    private val validatorNewUser: ValidatorRequest<NewUserRequest>,
    private val validatorUser: ValidatorRequest<UserRequest>,
    private val validatorUserLogin: ValidatorRequest<UserLoginRequest>,
    private val passwordService: PasswordService
) : UserService {

    override fun findByEmail(email: String): User {
        return userRepository.findByEmail(email)
    }

    override fun update(id: Int, userRequest: UserRequest, role: UserRole, email: String): User {
        validatorUser.validate(userRequest)
        val userToBeModified = userRepository.get(id)
        val userDTO = userRequest.toUser()
        try {
            val user = userRepository.findByEmail(userDTO.email)
            // if found an email in another user, cannot update the email to other that exists
            if (user.id != id) {
                throw EmailAlreadyExistsException()
            } else {
                throw UserNotFoundException()
            }
        } catch (exception: UserNotFoundException) {
            val newUserDTO = User(
                userToBeModified.id,
                userDTO.email,
                userToBeModified.password,
                userDTO.birthDate,
                userDTO.gender,
                userDTO.name,
                userDTO.phone,
                userDTO.role,
                userToBeModified.creationDate,
                DateTime.now(),
                jwtService.sign(userDTO.email, userDTO.role)
            )

            if (role == UserRole.ADMIN) {
                return userRepository.update(id, newUserDTO)
            }

            if (userToBeModified.email == email) {
                if (userToBeModified.role == newUserDTO.role) {
                    return userRepository.update(id, newUserDTO)
                }
                throw UnauthorizedRoleChangeException()
            }
            throw UnauthorizedDifferentUserChangeException()
        }
    }

    override fun login(userLoginRequest: UserLoginRequest): User {
        validatorUserLogin.validate(userLoginRequest)
        val newUserLogin = userLoginRequest.toUserLogin()
        val user = userRepository.findByEmail(newUserLogin.email)
        if (passwordService.verify(newUserLogin.password, user.password)) {
            val token = jwtService.sign(user.email, user.role)
            val loggedUser = user.copy(token = token)
            userRepository.update(loggedUser.id!!, loggedUser)
            return userRepository.get(loggedUser.id)
        }
        throw UserNotFoundException()
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
                passwordService.encode(newUser.password),
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
}
