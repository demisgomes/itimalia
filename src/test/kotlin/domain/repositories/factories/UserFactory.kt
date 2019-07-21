package domain.repositories.factories

import domain.entities.Gender
import domain.entities.Roles
import domain.entities.UserDTO
import org.joda.time.DateTime

object UserFactory{
    fun sampleDTO(email:String = "myemail@email.com", password: String = "myPassword", role: Roles = Roles.ANYONE, token:String? = "My token", birthDate:DateTime = DateTime.now() ): UserDTO{
        return UserDTO(
            id = null,
            email = email,
            password = password,
            birthDate = birthDate,
            gender = Gender.UNDEFINED,
            name = "Usuario",
            phone = "8199999999",
            role = role,
            creationDate = DateTime.now(),
            modificationDate = DateTime.now(),
            token = token
        )
    }
}