package domain.services

import domain.entities.*
import domain.exceptions.*
import domain.jwt.JWTUtils
import domain.repositories.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.util.*
import kotlin.test.assertEquals


class UserServiceTest{

    private lateinit var userRepositoryMock: UserRepository
    private lateinit var newUserDTO: UserDTO
    private lateinit var expectedUserDTO:UserDTO
    private lateinit var expectedAdminModifiedUserDTO:UserDTO
    private lateinit var dateTime: DateTime
    private lateinit var jwtUtils: JWTUtils
    private lateinit var actualDateTime: DateTime
    private lateinit var invalidUserLogin: UserLogin
    private lateinit var updatedAdminUserDTO:UserDTO
    private lateinit var expectedModifiedUserDTO:UserDTO
    private lateinit var validUserLogin: UserLogin

    private lateinit var updatedUserDTO:UserDTO

    @get:Rule
    val expectedEx = ExpectedException.none()

    @Before
    fun setup() {
        val formatter = DateTimeFormat.forPattern("dd/mm/yyyy")
        dateTime=formatter.parseDateTime("01/01/1990")
        mockkStatic(Calendar::class)
        actualDateTime= DateTime.now()


        updatedAdminUserDTO=UserDTO(
            1,
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

        updatedUserDTO=UserDTO(
            1,
            "newUser@domain.com",
            "password123",
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
            Roles.USER,
            actualDateTime,
            actualDateTime,
            "token_test"
        )
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

        expectedModifiedUserDTO = UserDTO(
            1,
            "newUser@domain.com",
            "password123",
            dateTime,
            Gender.MASC,
            "New User",
            "81823183183",
            Roles.USER,
            actualDateTime,
            actualDateTime,
            "token_test"
        )

        expectedAdminModifiedUserDTO = UserDTO(
            1,
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

        invalidUserLogin= UserLogin("myemail.com", "password")

        validUserLogin= UserLogin("newUser@domain.com", "password")

        userRepositoryMock= mockk(relaxed = true)

        jwtUtils= mockk(relaxed = true)
    }


    @Test
    fun `when a valid user without admin permissions request a sign up, register it`(){
        val user = NewUser(
            "newUser@domain.com",
            "password",
            dateTime,
            Gender.MASC,
            "New User",
            "81823183183"
        )

        every { DateTime.now() }.returns(actualDateTime)

        every { userRepositoryMock.findByEmail(newUserDTO.email) }.throws(UserNotFoundException())

        every { jwtUtils.sign(newUserDTO.email, newUserDTO.role, 5) }.returns("token_test")

        every { userRepositoryMock.add(newUserDTO)  }.returns(expectedUserDTO)

        val userDTO=UserServiceImpl(userRepositoryMock, jwtUtils).add(user)

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

        UserServiceImpl(userRepositoryMock, jwtUtils).add(user)
    }

    @Test
    fun `when an user without admin permission tries to modify its account should return the modified user`(){

        every { DateTime.now() }.returns(actualDateTime)

        every { jwtUtils.sign(updatedUserDTO.email, updatedUserDTO.role, 5) }.returns("token_test")

        every { userRepositoryMock.get(1)}.returns(expectedUserDTO)
        every { userRepositoryMock.update(1,updatedUserDTO) }.returns(expectedModifiedUserDTO)


        val userDTO=UserServiceImpl(userRepositoryMock,jwtUtils).update(1,updatedUserDTO, Roles.USER, updatedUserDTO.email)

        assertEquals(expectedModifiedUserDTO,userDTO)
    }

    @Test
    fun `when an user with admin permission tries to modify any account should return the modified user`(){

        //given

        every { DateTime.now() }.returns(actualDateTime)

        every { jwtUtils.sign(updatedAdminUserDTO.email, updatedAdminUserDTO.role, 5) }.returns("token_test")

        every { userRepositoryMock.get(1)}.returns(expectedUserDTO)
        every { userRepositoryMock.update(1,updatedAdminUserDTO) }.returns(expectedAdminModifiedUserDTO)


        //when
        val userDTO=UserServiceImpl(userRepositoryMock,jwtUtils).update(1,updatedAdminUserDTO, Roles.ADMIN, updatedAdminUserDTO.email+"A")

        //then
        assertEquals(expectedAdminModifiedUserDTO,userDTO)
    }

    @Test(expected = UnauthorizedDifferentUserChangeException::class)
    fun `when an user without admin permission tries to modify another account should expect UnauthorizedDifferentUserChangeException`(){
        //given

        every { DateTime.now() }.returns(actualDateTime)

        every { jwtUtils.sign(updatedAdminUserDTO.email, updatedAdminUserDTO.role, 5) }.returns("token_test")

        every { userRepositoryMock.get(1)}.returns(expectedUserDTO)


        //when
        val userDTO=UserServiceImpl(userRepositoryMock,jwtUtils).update(1,updatedAdminUserDTO, Roles.USER, updatedAdminUserDTO.email+"A")

        //then
        assertEquals(expectedAdminModifiedUserDTO,userDTO)
    }

    @Test(expected = UnauthorizedRoleChangeException::class)
    fun `when an user without admin permission tries to modify its account changing from user to admin should expect UnauthorizedRoleChangeException`(){
        //given

        every { DateTime.now() }.returns(actualDateTime)

        every { jwtUtils.sign(updatedAdminUserDTO.email, updatedAdminUserDTO.role, 5) }.returns("token_test")

        every { userRepositoryMock.get(1)}.returns(expectedUserDTO)
        //every { userRepositoryMock.update(1,updatedAdminUserDTO) }.returns(expectedAdminModifiedUserDTO)


        //when
        val userDTO=UserServiceImpl(userRepositoryMock,jwtUtils).update(1,updatedAdminUserDTO, Roles.USER, updatedAdminUserDTO.email)

        //then
        assertEquals(expectedAdminModifiedUserDTO,userDTO)
    }

    @Test(expected = UnmodifiedUserException::class)
    fun `when an user without admin permission tries to modify its account but without modifications should expect UnmodifiedUserException`(){
        //given

        every { DateTime.now() }.returns(actualDateTime)

        every { jwtUtils.sign(expectedUserDTO.email, expectedUserDTO.role, 5) }.returns("token_test")

        every { userRepositoryMock.get(1)}.returns(expectedUserDTO)


        //when
        UserServiceImpl(userRepositoryMock,jwtUtils).update(1,expectedUserDTO, Roles.USER, expectedUserDTO.email)

        //then expect UnmodifiedUserException
    }

    @Test(expected = UnmodifiedUserException::class)
    fun `when an user with admin permission tries to modify its account but without modifications should expect UnmodifiedUserException`(){
        //given

        every { DateTime.now() }.returns(actualDateTime)

        every { jwtUtils.sign(expectedAdminModifiedUserDTO.email, expectedAdminModifiedUserDTO.role, 5) }.returns("token_test")

        every { userRepositoryMock.get(1)}.returns(expectedAdminModifiedUserDTO)

        //when
        UserServiceImpl(userRepositoryMock,jwtUtils).update(1,expectedAdminModifiedUserDTO, Roles.ADMIN, expectedAdminModifiedUserDTO.email)

        //then
        //assertEquals(expectedAdminModifiedUserDTO,userDTO)
    }

    @Test(expected = UnmodifiedUserException::class)
    fun `when an user with admin permission tries to modify another account but without modifications should expect UnmodifiedUserException`(){
        //given

        every { DateTime.now() }.returns(actualDateTime)

        //every { jwtUtils.sign(expectedAdminModifiedUserDTO.email, expectedAdminModifiedUserDTO.role, 5) }.returns("token_test")

        every { userRepositoryMock.get(1)}.returns(expectedUserDTO)
        //every { userRepositoryMock.update(1,updatedAdminUserDTO) }.returns(expectedAdminModifiedUserDTO)

        //when
        UserServiceImpl(userRepositoryMock,jwtUtils).update(1,expectedUserDTO, Roles.ADMIN, updatedAdminUserDTO.email)

        //then return exception
    }

    @Test
    fun `when a user without admin permissions with invalid gender tries sign in, expect ValidationException with fields gender = invalid gender`(){
        val newUser = NewUser(
            "newUser@domain.com",
            "password",
            dateTime,
            null,
            "New User",
            "81823183183"
        )

        every { userRepositoryMock.findByEmail(newUser.email) }.throws(UserNotFoundException())

        expectedEx.expect(ValidationException::class.java)
        expectedEx.expectMessage("The validation does not successful in following field(s): {gender=[invalid gender]}")

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
        expectedEx.expectMessage("The validation does not successful in following field(s): {birthDate=[invalid birthDate]}")
        UserServiceImpl(userRepositoryMock, jwtUtils).add(newUser)
    }

    @Test
    fun `when a user with valid id was requested, return it`(){
        val expectedUserDTO = UserDTO(
            56415,
            "newUser@domain.com",
            "password",
            dateTime,
            Gender.MASC,
            "New User",
            "81823183183",
            Roles.USER,
            dateTime,
            DateTime.now(),
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
        expectedEx.expectMessage("The validation does not successful in following field(s): {email=[invalid email]}")
        UserServiceImpl(userRepositoryMock, jwtUtils).login(invalidUserLogin)
    }

    @Test
    fun `when an user tries login with a valid email and password and them matches with a user, return the signed user`() {
        //given validUserLogin

        //when
        every { userRepositoryMock.findByCredentials(validUserLogin.email, validUserLogin.password) }.returns(expectedUserDTO)

        val userDTO=UserServiceImpl(userRepositoryMock, jwtUtils).login(validUserLogin)

        //then
        assertEquals(expectedUserDTO,userDTO)
    }

    @Test(expected = UserNotFoundException::class)
    fun `when an user tries login with a valid email and password but not matches with any user, should expectreturn UserNotFoundException`() {
        //given validUserLogin

        //when
        every { userRepositoryMock.findByCredentials(validUserLogin.email, validUserLogin.password) }.throws(UserNotFoundException())
        UserServiceImpl(userRepositoryMock, jwtUtils).login(validUserLogin)

        //then UserNotFoundException
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

