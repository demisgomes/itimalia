package domain.services

import domain.entities.Gender
import domain.entities.NewUser
import domain.entities.Roles
import domain.entities.UserDTO
import domain.exceptions.EmailAlreadyExistsException
import domain.exceptions.UserNotFoundException
import domain.jwt.JWTUtils
import domain.repositories.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.NoSuchElementException
import kotlin.test.assertEquals

class AdminServiceTest{

    private lateinit var userRepositoryMock: UserRepository
    private lateinit var newUserDTO: UserDTO
    private lateinit var dateTime: DateTime
    private lateinit var actualDateTime: DateTime
    private lateinit var jwtUtils: JWTUtils
    private lateinit var expectedUserDTO:UserDTO

    @Before
    fun setup() {
        val formatter = DateTimeFormat.forPattern("dd/mm/yyyy")
        dateTime=formatter.parseDateTime("01/01/1990")
        mockkStatic(DateTime::class)
        actualDateTime= DateTime.now()

        expectedUserDTO = UserDTO(
            1,
            "newUser@domain.com",
            "password",
            dateTime,
            Gender.MASC,
            "New User",
            "81823183183",
            Roles.USER,
            actualDateTime,
            actualDateTime,
            "token_test"
        )

        newUserDTO = UserDTO(
            null,
            "newUser@domain.com",
            "password",
            dateTime,
            Gender.MASC,
            "New User",
            "81823183183",
            Roles.ADMIN,
            actualDateTime,
            actualDateTime,
            "token_test"
        )

        userRepositoryMock= mockk(relaxed = true)

        jwtUtils= mockk(relaxed = true)
    }


    @Test
    fun `when a valid user with admin permissions request a sign up, register it`(){
        val user = NewUser(
            "newUser@domain.com",
            "password",
            dateTime,
            Gender.MASC,
            "New User",
            "81823183183"
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
            actualDateTime,
            actualDateTime,
            "token_test"
        )

        every { DateTime.now() }.returns(actualDateTime)

        every { userRepositoryMock.findByEmail(newUserDTO.email) }.throws(NoSuchElementException())

        every { jwtUtils.sign(newUserDTO.email, newUserDTO.role, 5) }.returns("token_test")

        //adminUserDTO.token="token_test"

        every { userRepositoryMock.add(newUserDTO)  }.returns(expectedUserDTO)

        val userDTO=AdminServiceImpl(userRepositoryMock, jwtUtils).add(user)

        assertEquals(expectedUserDTO,userDTO)
    }

    @Test(expected = EmailAlreadyExistsException::class)
    fun `when a valid user request a sign up but the email already exists, should expect EmailAlreadyExistsException`(){
        val user = NewUser(
            "newUser@domain.com",
            "password",
            dateTime,
            Gender.MASC,
            "New User",
            "81823183183"
        )

        val unexpectedUserDTO = expectedUserDTO

        every { DateTime.now() }.returns(actualDateTime)

        every { userRepositoryMock.findByEmail(newUserDTO.email) }.returns(unexpectedUserDTO)

        AdminServiceImpl(userRepositoryMock, jwtUtils).add(user)
    }

    @Test(expected = NoSuchElementException::class)
    fun `when a user with invalid id was requested, throws UserNotFoundException`(){
        val userException=NoSuchElementException()
        every { userRepositoryMock.get(844)  }.throws(userException)

        AdminServiceImpl(userRepositoryMock, jwtUtils).get(844)
    }

}