package domain.services

import domain.entities.Gender
import domain.entities.NewUser
import domain.entities.UserDTO
import domain.exceptions.InvalidGenderException
import domain.exceptions.ValidationException
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals


class UserServiceTest{

    lateinit var userServiceImpl: UserService

    @Before
    fun setup() {
        userServiceImpl = UserServiceImpl()
    }


    @Test
    fun `when a valid user request a sign up, register it`(){
        val user = NewUser(
            "newUser@domain.com",
            "password",
            Calendar.getInstance().time,
            Gender.MASC,
            "New User",
            "81823183183"
        )

        val expectedUserDTO: UserDTO = UserDTO(
            1,
            "newUser@domain.com",
            "password",
            user.birthDate,
            Gender.MASC,
            "New User",
            "81823183183",
            Calendar.getInstance().time,
            Calendar.getInstance().time
        )
        val userDTO=userServiceImpl.add(user)

        assertEquals(expectedUserDTO.id,userDTO.id)
        assertEquals(expectedUserDTO.email,userDTO.email)
        assertEquals(expectedUserDTO.password,userDTO.password)
        assertEquals(expectedUserDTO.birthDate,userDTO.birthDate)
        assertEquals(expectedUserDTO.gender,userDTO.gender)
        assertEquals(expectedUserDTO.name,userDTO.name)
        assertEquals(expectedUserDTO.phone,userDTO.phone)
        assertEquals(userDTO.creationDate.time,expectedUserDTO.creationDate.time)
        assertEquals(userDTO.modificationDate.time,expectedUserDTO.modificationDate.time)
    }

    @Test(expected = ValidationException::class)
    fun `when a user with invalid gender tries sign in, expect ValidationException`(){
        val newUser = NewUser(
            "newUser@domain.com",
            "password",
            Calendar.getInstance().time,
            null,
            "New User",
            "81823183183"
        )

        val userDTO=userServiceImpl.add(newUser)
    }

    @Test(expected = ValidationException::class)
    fun `when a user with invalid birth date tries sign in, expect ValidationException`(){
        val newUser = NewUser(
            "newUser@domain.com",
            "password",
            null,
            Gender.MASC,
            "New User",
            "81823183183"
        )

        val userDTO=userServiceImpl.add(newUser)
    }
}

