package domain.services

import domain.entities.NewUser
import domain.entities.Roles
import domain.entities.UserDTO
import domain.entities.UserLogin
import domain.exceptions.*
import domain.jwt.JWTUtils
import domain.repositories.UserRepository
import domain.validation.UserValidation
import io.javalin.security.Role
import java.lang.IndexOutOfBoundsException

class UserServiceImpl(private val userRepository: UserRepository, private val jwtUtils: JWTUtils):UserService{
    override fun update(id: Int, userDTO: UserDTO, role: Role, email:String): UserDTO {
        val newUserDTO = UserDTO(
            userDTO.id,
            userDTO.email,
            userDTO.password,
            userDTO.birthDate,
            userDTO.gender,
            userDTO.name,
            userDTO.phone,
            userDTO.role,
            userDTO.creationDate,
            null,
            null
        )
        val userToBeModified = userRepository.get(id)

        if (role == Roles.ADMIN) {
            return updateCall(newUserDTO, userToBeModified, id)
        }

        if (userToBeModified.email == email) {
            if (userToBeModified.role == newUserDTO.role) {
                return updateCall(newUserDTO, userToBeModified, id)
            }
            throw UnauthorizedRoleChangeException()
        }
        throw UnauthorizedDifferentUserChangeException()
    }

    override fun login(newUserLogin: UserLogin): UserDTO {
        return userRepository.findByCredentials(newUserLogin.email,newUserLogin.password)
    }

    override fun add(newUser: NewUser): UserDTO {

        try{
            userRepository.findByEmail(newUser.email)
            throw EmailAlreadyExistsException()
        }
        catch (exception:UserNotFoundException){
            val newUserDTO=UserDTO(
                null,
                newUser.email,
                newUser.password,
                newUser.birthDate,
                newUser.gender,
                newUser.name,
                newUser.phone,
                Roles.USER,
                null,
                null,
                jwtUtils.sign(newUser.email,Roles.USER,5)
            )
            UserValidation().validate(newUserDTO)
            return userRepository.add(newUserDTO)
        }

    }

    override fun delete(id:Int, role:Role, email:String):UserDTO{
        val userToBeDeleted=userRepository.get(id)

        if(role==Roles.ADMIN) {
            return userRepository.delete(id)
        }
        if(userToBeDeleted.email == email){
            return userRepository.delete(id)
        }
        throw UnauthorizedDifferentUserChangeException()

    }

    override fun get(id: Int): UserDTO {
        return userRepository.get(id)
    }

    private fun updateCall(newUserDTO:UserDTO, userToBeModified:UserDTO, id:Int):UserDTO{
        newUserDTO.token = jwtUtils.sign(userToBeModified.email, newUserDTO.role, 5)
        UserValidation().validate(newUserDTO)
        return userRepository.update(id, newUserDTO)
    }

}