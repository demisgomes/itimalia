package domain.repositories

import domain.entities.UserDTO
import domain.exceptions.UnmodifiedUserException
import domain.exceptions.UserNotFoundException
import java.lang.IndexOutOfBoundsException

class UserRepositoryImpl:UserRepository{
    override fun findByCredentials(email: String, password: String): UserDTO {
        return userList.values.filter {
                it.email==email &&
                it.password == password
            }.get(0)

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
        val originalUser=get(id)
        if(originalUser == userDTO){
            throw UnmodifiedUserException()
        }
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