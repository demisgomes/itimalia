package domain.services

import domain.entities.NewUser
import domain.entities.UserDTO
import domain.repositories.UserRepository
import domain.validation.UserValidation

class UserServiceImpl(private val userRepository: UserRepository):UserService{
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
        return userRepository.add(newUserDTO)
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

        return userRepository.update(id, newUserDTO)
    }

    override fun delete(id:Int):UserDTO{
        return userRepository.delete(id)
    }

    override fun get(id: Int): UserDTO {
        return userRepository.get(id)
    }


}