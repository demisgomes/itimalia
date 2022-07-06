package com.abrigo.itimalia.factories

import com.abrigo.itimalia.domain.entities.user.Gender
import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.NewUserRequest
import com.abrigo.itimalia.domain.entities.user.User
import com.abrigo.itimalia.domain.entities.user.UserLogin
import com.abrigo.itimalia.domain.entities.user.UserLoginRequest
import com.abrigo.itimalia.domain.entities.user.UserRequest
import com.abrigo.itimalia.domain.entities.user.UserRole
import org.joda.time.DateTime

object UserFactory {
    fun sampleDTO(id: Int? = 1, email: String = "myemail@email.com", password: String = "myPassword", role: UserRole = UserRole.ANYONE, token: String? = "My token", birthDate: DateTime = DateTime.now(), creationDate: DateTime = DateTime.now(), modificationDate: DateTime = DateTime.now()): User {
        return User(
            id = id,
            email = email,
            password = password,
            birthDate = birthDate,
            gender = Gender.NOT_DECLARED,
            name = "Usuario",
            phone = "8199999999",
            role = role,
            creationDate = creationDate,
            modificationDate = modificationDate,
            token = token ?: throw IllegalArgumentException()
        )
    }

    fun sampleNew(email: String = "myemail@email.com", password: String = "myPassword", birthDate: DateTime = DateTime.now()): NewUser {
        return NewUser(
            email = email,
            password = password,
            birthDate = birthDate,
            gender = Gender.NOT_DECLARED,
            name = "Usuario",
            phone = "8199999999"
        )
    }

    fun sampleNewRequest(email: String = "myemail@email.com", password: String = "myPassword", birthDate: DateTime = DateTime.now()): NewUserRequest {
        return NewUserRequest(
            email = email,
            password = password,
            birthDate = birthDate,
            gender = Gender.NOT_DECLARED,
            name = "Usuario",
            phone = "8199999999"
        )
    }

    fun sampleLogin(email: String = "myemail@email.com", password: String = "myPassword"): UserLogin {
        return UserLogin(
            email = email,
            password = password
        )
    }

    fun sampleLoginRequest(email: String = "myemail@email.com", password: String = "myPassword"): UserLoginRequest {
        return UserLoginRequest(
            email = email,
            password = password
        )
    }

    fun sampleDTORequest(id: Int? = 1, email: String = "myemail@email.com", password: String = "myPassword", role: UserRole = UserRole.ANYONE, token: String? = "My token", birthDate: DateTime = DateTime.now(), creationDate: DateTime = DateTime.now(), modificationDate: DateTime = DateTime.now()): UserRequest {
        return UserRequest(
            id = id,
            email = email,
            password = password,
            birthDate = birthDate,
            gender = Gender.NOT_DECLARED,
            name = "Usuario",
            phone = "8199999999",
            role = role,
            creationDate = creationDate,
            modificationDate = modificationDate,
            token = token
        )
    }
}
