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
import org.junit.Before
import org.junit.Test
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.test.assertEquals

class AdminServiceTest{

    private lateinit var userRepositoryMock: UserRepository
    private lateinit var newUserDTO: UserDTO
    private lateinit var date: Date
    private lateinit var actualCalendar: Calendar
    private lateinit var jwtUtils: JWTUtils
    private lateinit var expectedUserDTO:UserDTO

    @Before
    fun setup() {
        val formatter: DateFormat = SimpleDateFormat("dd/mm/yyyy")
        date=formatter.parse("01/01/1990")
        mockkStatic(Calendar::class)
        actualCalendar=Calendar.getInstance()

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

        newUserDTO = UserDTO(
            null,
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

        userRepositoryMock= mockk(relaxed = true)

        jwtUtils= mockk(relaxed = true)
    }


    @Test
    fun `when a valid user with admin permissions request a sign up, register it`(){
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
            user.birthDate,
            Gender.MASC,
            "New User",
            "81823183183",
            Roles.ADMIN,
            actualCalendar.time,
            actualCalendar.time,
            "token_test"
        )

        every { Calendar.getInstance() }.returns(actualCalendar)

        every { userRepositoryMock.findByEmail(newUserDTO.email) }.throws(UserNotFoundException())

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
            date,
            Gender.MASC,
            "New User",
            "81823183183"
        )

        val unexpectedUserDTO = expectedUserDTO

        every { Calendar.getInstance() }.returns(actualCalendar)

        every { userRepositoryMock.findByEmail(newUserDTO.email) }.returns(unexpectedUserDTO)

        AdminServiceImpl(userRepositoryMock, jwtUtils).add(user)
    }

    @Test(expected = UserNotFoundException::class)
    fun `when a user with invalid id was requested, throws UserNotFoundException`(){
        val userException=UserNotFoundException()
        every { userRepositoryMock.get(844)  }.throws(userException)

        AdminServiceImpl(userRepositoryMock, jwtUtils).get(844)
    }

}