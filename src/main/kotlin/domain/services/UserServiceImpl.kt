package domain.services

import domain.entities.NewUser
import domain.entities.UserDTO
import domain.validation.UserValidation
import domain.validation.Validation
import domain.validation.validGender
import java.util.*
import javax.xml.bind.ValidationException

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
            Calendar.getInstance().time,
            Calendar.getInstance().time
        )

        val userValidation=UserValidation()

        userValidation.validate(newUserDTO)

        return newUserDTO
    }
}