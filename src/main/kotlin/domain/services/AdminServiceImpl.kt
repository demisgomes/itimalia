package domain.services

import domain.entities.NewUser
import domain.entities.Roles
import domain.entities.UserDTO
import domain.exceptions.EmailAlreadyExistsException
import domain.exceptions.UserNotFoundException
import domain.jwt.JWTUtils
import domain.repositories.UserRepository
import domain.validation.UserValidation
import java.util.*

class AdminServiceImpl(private val userRepository: UserRepository, private val jwtUtils: JWTUtils):AdminService{
    override fun add(newUser: NewUser): UserDTO {
        try{
            userRepository.findByEmail(newUser.email)
            throw EmailAlreadyExistsException()
        }
        catch (exception: UserNotFoundException){
            val actualDate = Calendar.getInstance().time
            val newUserDTO=UserDTO(
                null,
                newUser.email,
                newUser.password,
                newUser.birthDate,
                newUser.gender,
                newUser.name,
                newUser.phone,
                Roles.ADMIN,
                Calendar.getInstance().time,
                actualDate,
                jwtUtils.sign(newUser.email, Roles.ADMIN,5)
            )
            UserValidation().validate(newUserDTO)
            return userRepository.add(newUserDTO)
        }
    }

    override fun get(id: Int): UserDTO {
        return userRepository.get(id)
    }

}