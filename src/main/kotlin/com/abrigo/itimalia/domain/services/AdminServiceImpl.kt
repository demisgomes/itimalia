package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.User
import com.abrigo.itimalia.domain.entities.user.UserRole
import com.abrigo.itimalia.domain.jwt.JWTService
import com.abrigo.itimalia.domain.repositories.UserRepository
import org.joda.time.DateTime

class AdminServiceImpl(private val userRepository: UserRepository, private val jwtService: JWTService, private val passwordService: PasswordService) : AdminService {
    override fun add(newUser: NewUser): User {
        val actualDate = DateTime.now()
        val newUserDTO = User(
            null,
            newUser.email,
            passwordService.encode(newUser.password),
            newUser.birthDate,
            newUser.gender,
            newUser.name,
            newUser.phone,
            UserRole.ADMIN,
            actualDate,
            actualDate,
            jwtService.sign(newUser.email, UserRole.ADMIN)
        )
        return userRepository.add(newUserDTO)
    }

    override fun get(id: Int): User {
        return userRepository.get(id)
    }
}
