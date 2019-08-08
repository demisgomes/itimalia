package domain.services

import domain.entities.user.NewUser
import domain.entities.Roles
import domain.entities.user.UserDTO
import domain.exceptions.EmailAlreadyExistsException
import domain.exceptions.UnauthorizedAdminRoleException
import domain.exceptions.UserNotFoundException
import domain.jwt.JWTUtils
import domain.repositories.UserRepository
import domain.repositories.factories.UserFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class AdminServiceTest{

    private lateinit var userRepositoryMock: UserRepository
    private lateinit var newUserDTO: UserDTO
    private lateinit var birthDate: DateTime
    private lateinit var actualDateTime: DateTime
    private lateinit var jwtUtils: JWTUtils
    private lateinit var expectedUserDTO: UserDTO
    private lateinit var adminService: AdminService
    private lateinit var newUser: NewUser

    @Before
    fun setup() {
        val formatter = DateTimeFormat.forPattern("dd/mm/yyyy")
        birthDate=formatter.parseDateTime("01/01/1990")
        mockkStatic(DateTime::class)
        actualDateTime= DateTime.now()
        newUserDTO = UserFactory.sampleDTO(id = null, birthDate = birthDate, creationDate = actualDateTime, modificationDate = actualDateTime)
        expectedUserDTO = newUserDTO.copy(id = 1, token = "token_test")

        newUser = UserFactory.sampleNew(birthDate = birthDate)

        userRepositoryMock= mockk(relaxed = true)

        jwtUtils= mockk(relaxed = true)

        adminService = AdminServiceImpl(userRepositoryMock, jwtUtils)
    }


    @Test
    fun `when a valid user with admin permissions request a sign up, register it`(){

        every { DateTime.now() }.returns(actualDateTime)

        every { userRepositoryMock.findByEmail(newUserDTO.email) }.throws(UserNotFoundException())

        every { jwtUtils.sign(newUserDTO.email, Roles.ADMIN, 5) }.returns("token_test")

        every { userRepositoryMock.add(newUserDTO.copy(role = Roles.ADMIN, token = "token_test"))  }.returns(expectedUserDTO)

        val userDTO=adminService.add(newUser, Roles.ADMIN)

        assertEquals(expectedUserDTO,userDTO)
    }

    @Test(expected = EmailAlreadyExistsException::class)
    fun `when a valid user request a sign up but the email already exists, should expect EmailAlreadyExistsException`(){
        val unexpectedUserDTO = expectedUserDTO

        every { DateTime.now() }.returns(actualDateTime)

        every { userRepositoryMock.findByEmail(newUserDTO.email) }.returns(unexpectedUserDTO)

        adminService.add(newUser, Roles.ADMIN)
    }

    @Test(expected = UnauthorizedAdminRoleException::class)
    fun `when a valid user tries to register a new user but does not have admin permissions, should expect UnauthorizedAdminRoleException`(){
        adminService.add(newUser, Roles.USER)
    }

    @Test(expected = NoSuchElementException::class)
    fun `when a user with invalid id was requested, throws UserNotFoundException`(){
        val userException=NoSuchElementException()
        every { userRepositoryMock.get(844)  }.throws(userException)

        adminService.get(844)
    }

}