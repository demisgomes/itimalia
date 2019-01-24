package domain.services

import domain.entities.NewUser
import domain.entities.UserDTO
import domain.validation.UserValidation
import java.util.*

class UserServiceImpl:UserService{

    val userList:ArrayList<UserDTO> = ArrayList()

    override fun add(newUser: NewUser): UserDTO {
        val newUserDTO=UserDTO(
            userList.size+1,
            newUser.email,
            newUser.password,
            newUser.birthDate,
            newUser.gender,
            newUser.name,
            newUser.phone,
            newUser.admin,
            Calendar.getInstance().time,
            Calendar.getInstance().time
        )


        UserValidation().validate(newUserDTO)

        return newUserDTO
    }
}