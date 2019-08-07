package domain.repositories.factories

import domain.entities.*
import org.joda.time.DateTime

object UserFactory{
    fun sampleDTO(email:String = "myemail@email.com", password: String = "myPassword", role: Roles = Roles.ANYONE, token:String? = "My token", birthDate:DateTime = DateTime.now(), creationDate:DateTime = DateTime.now(), modificationDate:DateTime = DateTime.now() ): UserDTO{
        return UserDTO(
            id = null,
            email = email,
            password = password,
            birthDate = birthDate,
            gender = Gender.UNDEFINED,
            name = "Usuario",
            phone = "8199999999",
            role = role,
            creationDate = creationDate,
            modificationDate = modificationDate,
            token = token
        )
    }

    fun sampleNew(email:String = "myemail@email.com", password: String = "myPassword", role: Roles = Roles.ANYONE, birthDate:DateTime = DateTime.now() ): NewUser{
        return NewUser(
            email = email,
            password = password,
            birthDate = birthDate,
            gender = Gender.UNDEFINED,
            name = "Usuario",
            phone = "8199999999"
        )
    }

    fun sampleLogin(email:String = "myemail@email.com", password: String = "myPassword"): UserLogin{
        return UserLogin(
            email = email,
            password = password
        )
    }
}