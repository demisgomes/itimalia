package domain.services

import domain.entities.*
import domain.exceptions.UnauthorizedDifferentUserChangeException
import domain.exceptions.UserNotFoundException
import domain.exceptions.ValidationException
import domain.jwt.JWTUtils
import domain.repositories.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.test.assertEquals


class UserServiceTest{

    private lateinit var userRepositoryMock: UserRepository
    private lateinit var newUserDTO: UserDTO
    private lateinit var expectedUserDTO:UserDTO
    private lateinit var expectedModifiedUserDTO:UserDTO
    private lateinit var date:Date
    private lateinit var jwtUtils: JWTUtils
    private lateinit var actualCalendar: Calendar
    private lateinit var invalidUserLogin: UserLogin

    @get:Rule
    val expectedEx = ExpectedException.none()

    @Before
    fun setup() {
        val formatter:DateFormat= SimpleDateFormat("dd/mm/yyyy")
        date=formatter.parse("01/01/1990")
        mockkStatic(Calendar::class)
        actualCalendar= Calendar.getInstance()

        newUserDTO = UserDTO(
            null,
            "newUser@domain.com",
            "password",
            date,
            Gender.MASC,
            "New User",
            "81823183183",
            Roles.USER,
            actualCalendar.time,
            actualCalendar.time,
            "token_test"
        )
        expectedUserDTO = UserDTO(
            1,
            "newUser@domain.com",
            "password",
            date,
            Gender.MASC,
            "New User",
            "81823183183",
            Roles.USER,
            actualCalendar.time,
            actualCalendar.time,
            "token_test"
        )

        expectedModifiedUserDTO = UserDTO(
            1,
            "newUser@domain.com",
            "password",
            date,
            Gender.MASC,
            "New User",
            "81823183183",
            Roles.ADMIN,
            actualCalendar.time,
            actualCalendar.time,
            "token_test"
        )

        invalidUserLogin= UserLogin("myemail.com", "password")

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

        every { Calendar.getInstance() }.returns(actualCalendar)

        every { userRepositoryMock.findByEmail(newUserDTO.email) }.throws(UserNotFoundException())

        every { jwtUtils.sign(newUserDTO.email, newUserDTO.role, 5) }.returns("token_test")

        every { userRepositoryMock.add(newUserDTO)  }.returns(expectedUserDTO)

        val userDTO=UserServiceImpl(userRepositoryMock, jwtUtils).add(user)

        assertEquals(expectedUserDTO,userDTO)
    }

    @Test
    fun `when a valid user will be sucessfully modified modify it`(){

        every { Calendar.getInstance() }.returns(actualCalendar)

        val updatedUserDTO=UserDTO(
            1,
            "newUser@domain.com",
            "password",
            date,
            Gender.MASC,
            "New User",
            "81823183183",
            Roles.ADMIN,
            actualCalendar.time,
            actualCalendar.time,
            "token_test"
        )

        every { jwtUtils.sign(updatedUserDTO.email, updatedUserDTO.role, 5) }.returns("token_test")

        every { userRepositoryMock.get(1)}.returns(expectedUserDTO)
        every { userRepositoryMock.update(1,updatedUserDTO) }.returns(expectedModifiedUserDTO)


        val userDTO=UserServiceImpl(userRepositoryMock,jwtUtils).update(1,updatedUserDTO, updatedUserDTO.role, updatedUserDTO.email)

        assertEquals(expectedModifiedUserDTO,userDTO)
    }

    @Test
    fun `when a user without admin permissions with invalid gender tries sign in, expect ValidationException with fields gender = invalid gender`(){
        val newUser = NewUser(
            "newUser@domain.com",
            "password",
            date,
            null,
            "New User",
            "81823183183"
        )

        every { userRepositoryMock.findByEmail(newUser.email) }.throws(UserNotFoundException())

        expectedEx.expect(ValidationException::class.java)
        expectedEx.expectMessage("The validation does not sucessfull in following field(s): {gender=[invalid gender]}")

        UserServiceImpl(userRepositoryMock, jwtUtils).add(newUser)
    }

    @Test
    fun `when a user without admin permissions with invalid birth date tries sign in, expect ValidationException with fields birthdate = invalid birthDate`(){
        val newUser = NewUser(
            "newUser@domain.com",
            "password",
            null,
            Gender.MASC,
            "New User",
            "81823183183"
        )
        every { userRepositoryMock.findByEmail(newUser.email) }.throws(UserNotFoundException())

        expectedEx.expect(ValidationException::class.java)
        expectedEx.expectMessage("The validation does not sucessfull in following field(s): {birthDate=[invalid birthDate]}")
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

    @Test
    fun `when an user tries login with an invalid email, should expect a Validation exception with field email=invalid email`() {
        expectedEx.expect(ValidationException::class.java)
        expectedEx.expectMessage("The validation does not sucessfull in following field(s): {email=[invalid email]}")
        UserServiceImpl(userRepositoryMock, jwtUtils).login(invalidUserLogin)
    }

    @Test
    fun `when an user without admin permission tries to delete your account should return no content with status 204`(){
        every { userRepositoryMock.get(1) }.returns(expectedUserDTO)
        every { userRepositoryMock.delete(1) }.returns(expectedUserDTO)

        val deletedUser=UserServiceImpl(userRepositoryMock,jwtUtils).delete(1, expectedUserDTO.role, expectedUserDTO.email)
        assertEquals(expectedUserDTO, deletedUser)
    }

    @Test
    fun `when an user with admin permission tries to delete any account should return no content with status 204`(){
        every { userRepositoryMock.get(1) }.returns(expectedUserDTO)
        every { userRepositoryMock.delete(1) }.returns(expectedUserDTO)

        val deletedUser=UserServiceImpl(userRepositoryMock,jwtUtils).delete(1, Roles.ADMIN, expectedUserDTO.email)
        assertEquals(expectedUserDTO, deletedUser)
    }

    @Test(expected = UnauthorizedDifferentUserChangeException::class)
    fun `when an user without admin permission tries to delete another account should expect UnauthorizedDifferentUserChangeException`(){
        every { userRepositoryMock.get(1) }.returns(expectedUserDTO)
        every { userRepositoryMock.delete(1) }.returns(expectedUserDTO)

        UserServiceImpl(userRepositoryMock,jwtUtils).delete(1, expectedUserDTO.role, expectedUserDTO.email+"A")
    }


}

