package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.Roles
import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.UserDTO
import com.abrigo.itimalia.domain.entities.user.UserLogin
import com.abrigo.itimalia.domain.exceptions.*
import com.abrigo.itimalia.domain.jwt.JWTUtils
import com.abrigo.itimalia.domain.repositories.UserRepository
import com.abrigo.itimalia.domain.validation.UserLoginValidator
import com.abrigo.itimalia.domain.validation.UserValidator
import io.javalin.core.security.Role
import org.joda.time.DateTime

class UserServiceImpl(private val userRepository: UserRepository, private val jwtUtils: JWTUtils):UserService{
    override fun update(id: Int, userDTO: UserDTO, role: Role, email:String): UserDTO {
        val userToBeModified = userRepository.get(id)
        try{
            val user = userRepository.findByEmail(userDTO.email)
            //if found an email in another user, cannot update the email to other that exists
            if(user.id != id){
                throw EmailAlreadyExistsException()
            }
            else{
                throw UserNotFoundException()
            }
        }
        catch (exception:UserNotFoundException){
            val newUserDTO = UserDTO(
                userToBeModified.id,
                userDTO.email,
                userDTO.password,
                userDTO.birthDate,
                userDTO.gender,
                userDTO.name,
                userDTO.phone,
                userDTO.role,
                userToBeModified.creationDate,
                userToBeModified.modificationDate,
                userToBeModified.token
            )

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

    }

    override fun login(newUserLogin: UserLogin): UserDTO {
        UserLoginValidator().validate(newUserLogin)
        val user = userRepository.findByCredentials(newUserLogin.email,newUserLogin.password)
        val token = jwtUtils.sign(user.email, user.role, 5)
        val loggedUser = user.copy(token = token)
        userRepository.update(loggedUser.id!!, loggedUser)
        return userRepository.get(loggedUser.id!!)
    }

    override fun add(newUser: NewUser): UserDTO {
        try{
            userRepository.findByEmail(newUser.email)
            throw EmailAlreadyExistsException()
        }
        catch (exception:UserNotFoundException){
            val actualDate= DateTime.now()
            val newUserDTO= UserDTO(
                null,
                newUser.email,
                newUser.password,
                newUser.birthDate,
                newUser.gender,
                newUser.name,
                newUser.phone,
                Roles.USER,
                actualDate,
                actualDate,
                jwtUtils.sign(newUser.email, Roles.USER, 5)
            )
            //UserValidator().validate(newUserDTO)
            return userRepository.add(newUserDTO)
        }

    }

    override fun delete(id:Int, role:Role, email:String){
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

    private fun updateCall(newUserDTO: UserDTO, userToBeModified: UserDTO, id:Int): UserDTO {
        if(newUserDTO == userToBeModified){
            throw UnmodifiedUserException()
        }

        val actualDate=DateTime.now()
        val modifiedUserDTO= UserDTO(
            userToBeModified.id,
            newUserDTO.email,
            newUserDTO.password,
            newUserDTO.birthDate,
            newUserDTO.gender,
            newUserDTO.name,
            newUserDTO.phone,
            newUserDTO.role,
            userToBeModified.creationDate,
            actualDate,
            jwtUtils.sign(newUserDTO.email, newUserDTO.role, 5)
        )

        UserValidator().validate(newUserDTO)
        return userRepository.update(id, modifiedUserDTO)
    }

}