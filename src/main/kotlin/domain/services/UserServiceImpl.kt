package domain.services

import domain.entities.NewUser
import domain.entities.UserDTO
import domain.repositories.UserRepositoryImpl
import domain.validation.UserValidation
import java.util.*

class UserServiceImpl(private val userRepositoryImpl: UserRepositoryImpl):UserService{
    override fun add(newUser: NewUser): UserDTO {
        val newUserDTO=UserDTO(
            null,
            newUser.email,
            newUser.password,
            newUser.birthDate,
            newUser.gender,
            newUser.name,
            newUser.phone,
            newUser.admin,
            null,
            null
        )

        UserValidation().validate(newUserDTO)
        return userRepositoryImpl.add(newUserDTO)
    }

    override fun update(id: Int, userDTO: UserDTO): UserDTO {
        val newUserDTO=UserDTO(
            userDTO.id,
            userDTO.email,
            userDTO.password,
            userDTO.birthDate,
            userDTO.gender,
            userDTO.name,
            userDTO.phone,
            userDTO.admin,
            userDTO.creationDate,
            null
        )
        UserValidation().validate(newUserDTO)

        return userRepositoryImpl.update(id, newUserDTO)
    }

    override fun get(id: Int): UserDTO {
        return userRepositoryImpl.get(id)
    }


}