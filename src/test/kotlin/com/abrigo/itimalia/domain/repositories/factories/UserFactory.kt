package com.abrigo.itimalia.domain.repositories.factories

import com.abrigo.itimalia.domain.entities.Gender
import com.abrigo.itimalia.domain.entities.Roles
import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.NewUserRequest
import com.abrigo.itimalia.domain.entities.user.UserDTO
import com.abrigo.itimalia.domain.entities.user.UserDTORequest
import com.abrigo.itimalia.domain.entities.user.UserLogin
import com.abrigo.itimalia.domain.entities.user.UserLoginRequest
import org.joda.time.DateTime

object UserFactory{
    fun sampleDTO(id:Int? = 1, email:String = "myemail@email.com", password: String = "myPassword", role: Roles = Roles.ANYONE, token:String? = "My token", birthDate:DateTime = DateTime.now(), creationDate:DateTime = DateTime.now(), modificationDate:DateTime = DateTime.now() ): UserDTO {
        return UserDTO(
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

    fun sampleNew(email:String = "myemail@email.com", password: String = "myPassword", birthDate:DateTime = DateTime.now() ): NewUser {
        return NewUser(
            email = email,
            password = password,
            birthDate = birthDate,
            gender = Gender.NOT_DECLARED,
            name = "Usuario",
            phone = "8199999999"
        )
    }

    fun sampleNewRequest(email:String = "myemail@email.com", password: String = "myPassword", birthDate:DateTime = DateTime.now() ): NewUserRequest {
        return NewUserRequest(
            email = email,
            password = password,
            birthDate = birthDate,
            gender = Gender.NOT_DECLARED,
            name = "Usuario",
            phone = "8199999999"
        )
    }

    fun sampleLogin(email:String = "myemail@email.com", password: String = "myPassword"): UserLogin {
        return UserLogin(
            email = email,
            password = password
        )
    }

    fun sampleLoginRequest(email:String = "myemail@email.com", password: String = "myPassword"): UserLoginRequest {
        return UserLoginRequest(
            email = email,
            password = password
        )
    }

    fun sampleDTORequest(id:Int? = 1, email:String = "myemail@email.com", password: String = "myPassword", role: Roles = Roles.ANYONE, token:String? = "My token", birthDate:DateTime = DateTime.now(), creationDate:DateTime = DateTime.now(), modificationDate:DateTime = DateTime.now()): UserDTORequest {
        return UserDTORequest(
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