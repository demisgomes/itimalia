package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.Roles
import com.abrigo.itimalia.domain.entities.user.UserDTO
import com.abrigo.itimalia.domain.exceptions.EmailAlreadyExistsException
import com.abrigo.itimalia.domain.exceptions.UnauthorizedAdminRoleException
import com.abrigo.itimalia.domain.exceptions.UserNotFoundException
import com.abrigo.itimalia.domain.jwt.JWTService
import com.abrigo.itimalia.domain.repositories.UserRepository
import org.joda.time.DateTime

class AdminServiceImpl(private val userRepository: UserRepository, private val jwtService: JWTService) : AdminService {
    override fun add(newUser: NewUser, role: Roles): UserDTO {
        if (role != Roles.ADMIN) {
            throw UnauthorizedAdminRoleException()
        }
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
                Roles.ADMIN,
                actualDate,
                actualDate,
                jwtService.sign(newUser.email, Roles.ADMIN)
            )
            return userRepository.add(newUserDTO)
        }
    }

    override fun get(id: Int): UserDTO {
        return userRepository.get(id)
    }

}