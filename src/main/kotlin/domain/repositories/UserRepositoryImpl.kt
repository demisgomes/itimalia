package domain.repositories

import domain.entities.NewUser
import domain.entities.UserDTO
import domain.exceptions.UserNotFoundException

class UserRepositoryImpl:UserRepository{
    var userList:HashMap<Int, UserDTO> = HashMap()

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

    override fun get(id: Int): UserDTO {
        if(userList.containsKey(id)){
            return userList[id]!!
        }
        else{
            throw UserNotFoundException("User Not Found")
        }
    }

}