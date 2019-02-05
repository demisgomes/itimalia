package domain.services

import domain.entities.Gender
import domain.entities.NewUser
import domain.entities.Roles
import domain.entities.UserDTO
import domain.exceptions.UserNotFoundException
import domain.exceptions.ValidationException
import domain.jwt.JWTUtils
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
    private lateinit var jwtUtils: JWTUtils

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
            Roles.USER,
            null,
            null,
            "token_test"
        )

        userRepositoryImplMock= mockk(relaxed = true)

        jwtUtils= mockk(relaxed = true)
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
            Roles.USER
        )

        val expectedUserDTO = UserDTO(
            1,
            "newUser@domain.com",
            "password",
            date,
            Gender.MASC,
            "New User",
            "81823183183",
            Roles.USER,
            date,
            Calendar.getInstance().time,
            "token_test"
        )
        val newUserDTOWithoutToken=newUserDTO
        newUserDTOWithoutToken.token=null

        every { jwtUtils.sign(newUserDTOWithoutToken.email, newUserDTOWithoutToken.role, 60) }.returns("token_test")

        newUserDTOWithoutToken.token="token_test"

        every { userRepositoryImplMock.add(newUserDTOWithoutToken)  }.returns(expectedUserDTO)


        val userDTO=UserServiceImpl(userRepositoryImplMock, jwtUtils).add(user)

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
            Roles.ADMIN
        )

        val expectedUserDTO = UserDTO(
            1,
            "newUser@domain.com",
            "password",
            user.birthDate,
            Gender.MASC,
            "New User",
            "81823183183",
            Roles.ADMIN,
            Calendar.getInstance().time,
            Calendar.getInstance().time,
            "token_test"
        )

        val adminUserDTO=newUserDTO
        adminUserDTO.role=Roles.ADMIN
        adminUserDTO.token=null

        every { jwtUtils.sign(adminUserDTO.email, adminUserDTO.role, 60) }.returns("token_test")

        adminUserDTO.token="token_test"

        every { userRepositoryImplMock.add(adminUserDTO)  }.returns(expectedUserDTO)
        val userDTO=UserServiceImpl(userRepositoryImplMock, jwtUtils).add(user)

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
            Roles.ADMIN,
            date,
            null,
            "token_test"
        )

        val updatedUserDTO=newUserDTO
        updatedUserDTO.role=Roles.ADMIN
        updatedUserDTO.id=1
        updatedUserDTO.creationDate=date

        every { userRepositoryImplMock.update(1,updatedUserDTO) }.returns(expectedUserDTO)
        every { userRepositoryImplMock.get(1)}.returns(newUserDTO)

        val userDTO=UserServiceImpl(userRepositoryImplMock,jwtUtils).update(1,updatedUserDTO)

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
            Roles.USER
        )
        UserServiceImpl(userRepositoryImplMock, jwtUtils).add(newUser)
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
            Roles.USER
        )

        UserServiceImpl(userRepositoryImplMock, jwtUtils).add(newUser)
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
            Roles.USER,
            date,
            Calendar.getInstance().time,
            null
        )

        every { userRepositoryImplMock.get(56415)  }.returns(expectedUserDTO)


        assertEquals(expectedUserDTO, UserServiceImpl(userRepositoryImplMock, jwtUtils).get(56415))
    }


    @Test(expected = UserNotFoundException::class)
    fun `when a user with invalid id was requested, throws UserNotFoundException`(){
        val userException=UserNotFoundException()
        every { userRepositoryImplMock.get(844)  }.throws(userException)

        UserServiceImpl(userRepositoryImplMock, jwtUtils).get(844)
    }

}

