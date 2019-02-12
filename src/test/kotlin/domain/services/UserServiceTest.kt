package domain.services

import domain.entities.Gender
import domain.entities.NewUser
import domain.entities.Roles
import domain.entities.UserDTO
import domain.exceptions.UserNotFoundException
import domain.exceptions.ValidationException
import domain.jwt.JWTUtils
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

    private lateinit var userRepositoryMock: UserRepository
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

        userRepositoryMock= mockk(relaxed = true)

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
            "81823183183"
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
            null,
            null,
            "token_test"
        )
        val newUserDTOWithoutToken=newUserDTO
        newUserDTOWithoutToken.token=null

        every { userRepositoryMock.findByEmail(newUserDTOWithoutToken.email) }.throws(UserNotFoundException())

        every { jwtUtils.sign(newUserDTOWithoutToken.email, newUserDTOWithoutToken.role, 5) }.returns("token_test")

        newUserDTOWithoutToken.token="token_test"

        every { userRepositoryMock.add(newUserDTOWithoutToken)  }.returns(expectedUserDTO)


        val userDTO=UserServiceImpl(userRepositoryMock, jwtUtils).add(user)

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
            Calendar.getInstance().time,
            "token_test"
        )

        val updatedUserDTO=newUserDTO
        updatedUserDTO.role=Roles.ADMIN
        updatedUserDTO.id=1
        updatedUserDTO.creationDate=date

        every { jwtUtils.sign(updatedUserDTO.email, updatedUserDTO.role, 5) }.returns("token_test")


        //every { userRepositoryMock.findByEmail(newUserDTO.email) }.throws(UserNotFoundException())

        every { userRepositoryMock.update(1,updatedUserDTO) }.returns(expectedUserDTO)
        every { userRepositoryMock.get(1)}.returns(updatedUserDTO)



        val userDTO=UserServiceImpl(userRepositoryMock,jwtUtils).update(1,updatedUserDTO, updatedUserDTO.role, updatedUserDTO.email)

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
            "81823183183"
        )


        every { userRepositoryMock.findByEmail(newUser.email) }.throws(UserNotFoundException())
        UserServiceImpl(userRepositoryMock, jwtUtils).add(newUser)
    }

    @Test(expected = ValidationException::class)
    fun `when a user without admin permissions with invalid birth date tries sign in, expect ValidationException`(){
        val newUser = NewUser(
            "newUser@domain.com",
            "password",
            null,
            Gender.MASC,
            "New User",
            "81823183183"
        )


        every { userRepositoryMock.findByEmail(newUser.email) }.throws(UserNotFoundException())
        UserServiceImpl(userRepositoryMock, jwtUtils).add(newUser)
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

        every { userRepositoryMock.get(56415)  }.returns(expectedUserDTO)


        assertEquals(expectedUserDTO, UserServiceImpl(userRepositoryMock, jwtUtils).get(56415))
    }


    @Test(expected = UserNotFoundException::class)
    fun `when a user with invalid id was requested, throws UserNotFoundException`(){
        val userException=UserNotFoundException()
        every { userRepositoryMock.get(844)  }.throws(userException)

        UserServiceImpl(userRepositoryMock, jwtUtils).get(844)
    }

}

