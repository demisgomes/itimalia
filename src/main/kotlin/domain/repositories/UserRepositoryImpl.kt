package domain.repositories

import domain.entities.UserDTO
import domain.exceptions.EmailAlreadyExistsException
import domain.exceptions.InvalidCredentialsException
import domain.exceptions.UnmodifiedUserException
import domain.exceptions.UserNotFoundException
import java.lang.IndexOutOfBoundsException

class UserRepositoryImpl:UserRepository{
    override fun findByEmail(email: String):UserDTO {
        try{
            return userList.values.filter {
                it.email == email
            }[0]
        }
        catch (exception: IndexOutOfBoundsException){
            throw UserNotFoundException()
        }

    }

    override fun findByCredentials(email: String, password: String): UserDTO {
        try{
            return userList.values.filter {
                it.email == email &&
                        it.password == password
            }[0]
        }
        catch (exception: IndexOutOfBoundsException){
            throw UserNotFoundException()
        }

    }

    companion object {
        var userList:HashMap<Int, UserDTO> = HashMap()
    }


    override fun add(userDTO: UserDTO): UserDTO {
        val id:Int=userList.size+1
        userDTO.id=id
        userList[id] = userDTO
        return userDTO
    }

    override fun update(id: Int, userDTO: UserDTO): UserDTO {
        userList[id]=userDTO
        return userDTO
    }

    override fun delete(id: Int): UserDTO {
        val originalUser=get(id)
        userList.remove(id)
        return originalUser
    }

    override fun get(id: Int): UserDTO {
        if(userList.containsKey(id)){
            return userList[id]!!
        }
        else{
            throw UserNotFoundException()
        }
    }

}