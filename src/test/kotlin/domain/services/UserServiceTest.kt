package domain.services

import domain.entities.Gender
import domain.entities.NewUser
import domain.entities.UserDTO
import domain.exceptions.ValidationException
import domain.repositories.UserRepository
import domain.repositories.UserRepositoryImpl
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.test.assertEquals


class UserServiceTest{

    lateinit var userRepositoryImplMock: UserRepositoryImpl

    @Before
    fun setup() {
        userRepositoryImplMock= mockk(relaxed = true)
    }


    @Test
    fun `when a valid user without admin permissions request a sign up, register it`(){

        val formatter:DateFormat= SimpleDateFormat("dd/mm/yyyy") as DateFormat
        val date=formatter.parse("01/01/1990")
        val user = NewUser(
            "newUser@domain.com",
            "password",
            date,
            Gender.MASC,
            "New User",
            "81823183183",
            false
        )


        val expectedUserDTO = UserDTO(
            1,
            "newUser@domain.com",
            "password",
            date,
            Gender.MASC,
            "New User",
            "81823183183",
            false,
            date!!,
            Calendar.getInstance().time
        )

        every { userRepositoryImplMock.add(expectedUserDTO)  }.returns(expectedUserDTO)


        val userDTO=UserServiceImpl().add(user)

        assertEquals(expectedUserDTO.id,userDTO.id)
        assertEquals(expectedUserDTO.email,userDTO.email)
        assertEquals(expectedUserDTO.password,userDTO.password)
        assertEquals(expectedUserDTO.birthDate,userDTO.birthDate)
        assertEquals(expectedUserDTO.gender,userDTO.gender)
        assertEquals(expectedUserDTO.name,userDTO.name)
        assertEquals(expectedUserDTO.phone,userDTO.phone)
        assertEquals(expectedUserDTO.admin, userDTO.admin)
        //assertEquals(userDTO.creationDate.time,expectedUserDTO.creationDate.time)
        //assertEquals(userDTO.modificationDate.time,expectedUserDTO.modificationDate.time)
    }

    @Test
    fun `when a valid user with admin permissions request a sign up, register it`(){
        val formatter:DateFormat=SimpleDateFormat("dd/mm/yyyy")
        val date=formatter.parse("01/01/1990")
        val user = NewUser(
            "newUser@domain.com",
            "password",
            date,
            Gender.MASC,
            "New User",
            "81823183183",
            true
        )

        val expectedUserDTO: UserDTO = UserDTO(
            1,
            "newUser@domain.com",
            "password",
            user.birthDate,
            Gender.MASC,
            "New User",
            "81823183183",
            true,
            Calendar.getInstance().time,
            Calendar.getInstance().time
        )

        val userDTO=UserServiceImpl().add(user)

        assertEquals(expectedUserDTO.id,userDTO.id)
        assertEquals(expectedUserDTO.email,userDTO.email)
        assertEquals(expectedUserDTO.password,userDTO.password)
        assertEquals(expectedUserDTO.birthDate,userDTO.birthDate)
        assertEquals(expectedUserDTO.gender,userDTO.gender)
        assertEquals(expectedUserDTO.name,userDTO.name)
        assertEquals(expectedUserDTO.phone,userDTO.phone)
        assertEquals(expectedUserDTO.admin, userDTO.admin)
        assertEquals(userDTO.creationDate.time,expectedUserDTO.creationDate.time)
        assertEquals(userDTO.modificationDate.time,expectedUserDTO.modificationDate.time)
    }

    @Test
    fun `when a valid user will be sucessfully modified modify it`(){

        //mock this part
//        val formatter:DateFormat=SimpleDateFormat("dd/mm/yyyy")
//        val date=formatter.parse("01/01/1990")
//        val user = NewUser(
//            "newUser@domain.com",
//            "password",
//            date,
//            Gender.MASC,
//            "New User",
//            "81823183183",
//            true
//        )
//
//
//        val newUserDTO=userServiceImpl.add(user)
//
//
//        //the main method starts here
//        val userModified= NewUser("newUser@domain.com",
//            "password",
//            date,
//            Gender.FEM,
//            "New User",
//            "81823183183",
//            true)

        val formatter:DateFormat=SimpleDateFormat("dd/mm/yyyy")
        val date=formatter.parse("01/01/1990")
        val expectedUserDTO = UserDTO(
            1,
            "newUser@domain.com",
            "password",
            date,
            Gender.FEM,
            "New User",
            "81823183183",
            true,
            date,
            Calendar.getInstance().time
        )

        every { userRepositoryImplMock.update(1,expectedUserDTO) }.returns(expectedUserDTO)
        val userDTO=UserServiceImpl().update(1,expectedUserDTO)

        assertEquals(expectedUserDTO.id,userDTO.id)
        assertEquals(expectedUserDTO.email,userDTO.email)
        assertEquals(expectedUserDTO.password,userDTO.password)
        assertEquals(expectedUserDTO.birthDate,userDTO.birthDate)
        assertEquals(expectedUserDTO.gender,userDTO.gender)
        assertEquals(expectedUserDTO.name,userDTO.name)
        assertEquals(expectedUserDTO.phone,userDTO.phone)
        assertEquals(expectedUserDTO.admin, userDTO.admin)
        assertEquals(userDTO.creationDate.time,expectedUserDTO.creationDate.time)
        //assertEquals(userDTO.modificationDate.time,expectedUserDTO.modificationDate.time)
    }

    @Test(expected = ValidationException::class)
    fun `when a user without admin permissions with invalid gender tries sign in, expect ValidationException`(){
        val newUser = NewUser(
            "newUser@domain.com",
            "password",
            Calendar.getInstance().time,
            null,
            "New User",
            "81823183183",
            false
        )
        UserServiceImpl().add(newUser)
    }

    @Test(expected = ValidationException::class)
    fun `when a user without admin permissions with invalid birth date tries sign in, expect ValidationException`(){
        val newUser = NewUser(
            "newUser@domain.com",
            "password",
            null,
            Gender.MASC,
            "New User",
            "81823183183",
            false
        )

        UserServiceImpl().add(newUser)
    }


}

