package domain.services

import domain.entities.Gender
import domain.entities.NewUser
import domain.entities.UserDTO
import domain.exceptions.UserNotFoundException
import domain.exceptions.ValidationException
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

    private lateinit var userRepositoryImplMock: UserRepositoryImpl
    private lateinit var newUserDTO: UserDTO
    private lateinit var date:Date

    @Before
    fun setup() {
        val formatter:DateFormat= SimpleDateFormat("dd/mm/yyyy")
        date=formatter.parse("01/01/1990")
        newUserDTO = UserDTO(
            null,
            "newUser@domain.com",
            "password",
            date,
            Gender.MASC,
            "New User",
            "81823183183",
            false,
            null,
            null
        )

        userRepositoryImplMock= mockk(relaxed = true)

    }


    @Test
    fun `when a valid user without admin permissions request a sign up, register it`(){
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
            date,
            Calendar.getInstance().time
        )

        every { userRepositoryImplMock.add(newUserDTO)  }.returns(expectedUserDTO)


        val userDTO=UserServiceImpl(userRepositoryImplMock).add(user)

        assertEquals(expectedUserDTO,userDTO)
    }

    @Test
    fun `when a valid user with admin permissions request a sign up, register it`(){
        val user = NewUser(
            "newUser@domain.com",
            "password",
            date,
            Gender.MASC,
            "New User",
            "81823183183",
            true
        )

        val expectedUserDTO = UserDTO(
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

        val adminUserDTO=newUserDTO
        adminUserDTO.admin=true

        every { userRepositoryImplMock.add(adminUserDTO)  }.returns(expectedUserDTO)
        val userDTO=UserServiceImpl(userRepositoryImplMock).add(user)

        assertEquals(expectedUserDTO,userDTO)
    }

    @Test
    fun `when a valid user will be sucessfully modified modify it`(){

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
            null
        )

        val updatedUserDTO=newUserDTO
        updatedUserDTO.admin=true
        updatedUserDTO.id=1
        updatedUserDTO.creationDate=date

        every { userRepositoryImplMock.update(1,updatedUserDTO) }.returns(expectedUserDTO)

        val userDTO=UserServiceImpl(userRepositoryImplMock).update(1,updatedUserDTO)

        assertEquals(expectedUserDTO,userDTO)
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
        UserServiceImpl(userRepositoryImplMock).add(newUser)
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

        UserServiceImpl(userRepositoryImplMock).add(newUser)
    }

    @Test
    fun `when a user with valid id was requested, return it`(){
        val expectedUserDTO = UserDTO(
            56415,
            "newUser@domain.com",
            "password",
            date,
            Gender.MASC,
            "New User",
            "81823183183",
            false,
            date,
            Calendar.getInstance().time
        )

        every { userRepositoryImplMock.get(56415)  }.returns(expectedUserDTO)


        assertEquals(expectedUserDTO, UserServiceImpl(userRepositoryImplMock).get(56415))
    }


    @Test(expected = UserNotFoundException::class)
    fun `when a user with invalid id was requested, throws UserNotFoundException`(){
        val userException=UserNotFoundException("User not found")
        every { userRepositoryImplMock.get(844)  }.throws(userException)

        UserServiceImpl(userRepositoryImplMock).get(844)
    }

}

