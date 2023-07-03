package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.user.User
import com.abrigo.itimalia.domain.entities.user.UserLoginRequest
import com.abrigo.itimalia.domain.entities.user.UserRequest
import com.abrigo.itimalia.domain.entities.user.UserRole
import com.abrigo.itimalia.domain.entities.user.toUser
import com.abrigo.itimalia.domain.exceptions.EmailAlreadyExistsException
import com.abrigo.itimalia.domain.exceptions.UnauthorizedDifferentUserChangeException
import com.abrigo.itimalia.domain.exceptions.UnauthorizedRoleChangeException
import com.abrigo.itimalia.domain.exceptions.UserNotFoundException
import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.domain.jwt.JWTService
import com.abrigo.itimalia.domain.repositories.UserRepository
import com.abrigo.itimalia.domain.validation.Request
import com.abrigo.itimalia.domain.validation.ValidatorRequest
import com.abrigo.itimalia.factories.UserFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.util.Optional
import kotlin.test.assertEquals

class UserServiceTest {

    private lateinit var userRepositoryMock: UserRepository
    private lateinit var newUser: User
    private lateinit var expectedUser: User
    private lateinit var expectedAdminModifiedUser: User
    private lateinit var birthDate: DateTime
    private lateinit var jwtService: JWTService
    private lateinit var actualDateTime: DateTime
    private lateinit var invalidUserLogin: UserLoginRequest
    private lateinit var updatedAdminUser: User
    private lateinit var expectedModifiedUser: User
    private lateinit var validUserLogin: UserLoginRequest
    private lateinit var updatedUser: User
    private lateinit var expectedModifiedUserRequest: UserRequest
    private lateinit var expectedUserRequest: UserRequest
    private lateinit var updatedAdminUserRequest: UserRequest
    private lateinit var expectedAdminModifiedUserRequest: UserRequest
    private lateinit var newUserRequest: UserRequest
    private lateinit var updatedUserRequest: UserRequest
    private lateinit var userService: UserService
    private lateinit var validatorRequest: ValidatorRequest<Request>
    private val passwordServiceMock: PasswordService = mockk(relaxed = true)

    @get:Rule
    val expectedEx = ExpectedException.none()

    @Before
    fun setup() {
        validatorRequest = mockk(relaxed = true)
        val formatter = DateTimeFormat.forPattern("dd/mm/yyyy")
        birthDate = formatter.parseDateTime("01/01/1990")
        mockkStatic(DateTime::class)
        actualDateTime = DateTime.now()

        newUserRequest = UserFactory.sampleDTORequest(id = null, birthDate = birthDate, creationDate = actualDateTime, modificationDate = actualDateTime)
        newUser = newUserRequest.toUser()
        expectedUserRequest = newUserRequest.copy(id = 1, token = "token_test")
        expectedUser = expectedUserRequest.toUser().copy(password = "encodedPassword")
        updatedUserRequest = expectedUserRequest.copy(name = "New name")
        updatedUser = updatedUserRequest.toUser().copy(password = "encodedPassword")
        expectedAdminModifiedUserRequest = expectedUserRequest.copy(role = UserRole.ADMIN)
        expectedAdminModifiedUser = expectedAdminModifiedUserRequest.toUser()
        expectedModifiedUserRequest = expectedUserRequest.copy(password = expectedUserRequest.password + "123")
        expectedModifiedUser = expectedUserRequest.toUser()

        updatedAdminUserRequest = expectedUserRequest.copy(role = UserRole.ADMIN)
        updatedAdminUser = updatedAdminUserRequest.toUser().copy(password = "encodedPassword")

        invalidUserLogin = UserFactory.sampleLoginRequest(email = "myemail.com")

        validUserLogin = UserFactory.sampleLoginRequest()

        userRepositoryMock = mockk(relaxed = true)

        jwtService = mockk(relaxed = true)

        userService = UserServiceImpl(userRepositoryMock, jwtService, validatorRequest, passwordServiceMock)
    }

    @Test
    fun `when a valid user without admin permissions request a sign up, register it with an encoded password`() {
        val userRequest = UserFactory.sampleNewRequest(birthDate = birthDate)
        every { DateTime.now() }.returns(actualDateTime)

        every { passwordServiceMock.encode("myPassword") } returns "encodedPassword"

        every { jwtService.sign(newUser.email, UserRole.USER) }.returns("token_test")

        every { userRepositoryMock.add(newUser.copy(role = UserRole.USER, token = "token_test", password = "encodedPassword")) }.returns(expectedUser)

        val userDTO = userService.add(userRequest)

        assertEquals(expectedUser, userDTO)
    }

    @Test(expected = EmailAlreadyExistsException::class)
    fun `when a valid user request a sign up but the email already exists, should expect EmailAlreadyExistsException`() {
        val userRequest = UserFactory.sampleNewRequest(birthDate = birthDate)

        every { DateTime.now() }.returns(actualDateTime)

        every { passwordServiceMock.encode("myPassword") } returns "encodedPassword"

        every { jwtService.sign(newUser.email, UserRole.USER) }.returns("token_test")

        every {
            userRepositoryMock.add(
                newUser.copy(role = UserRole.USER, token = "token_test", password = "encodedPassword")
            )
        }.throws(EmailAlreadyExistsException())

        userService.add(userRequest)
        // then exception
    }

    @Test
    fun `when an user without admin permission tries to modify its account should return the modified user`() {
        every { DateTime.now() }.returns(actualDateTime)

        every { jwtService.sign(updatedUser.email, updatedUser.role) }.returns("token_test")

        every { userRepositoryMock.get(1) }.returns(expectedUser)
        every { userRepositoryMock.update(1, updatedUser) }.returns(expectedModifiedUser)

        val userDTO = userService.update(1, updatedUserRequest, UserRole.USER, updatedUser.email)

        assertEquals(expectedModifiedUser, userDTO)
    }

    @Test
    fun `when an user without admin permission tries to modify its account without change the email should return the modified user`() {
        every { DateTime.now() }.returns(actualDateTime)

        every { jwtService.sign(updatedUser.email, updatedUser.role) }.returns("token_test")

        every { userRepositoryMock.get(1) }.returns(expectedUser)

        every { userRepositoryMock.update(1, updatedUser) }.returns(expectedModifiedUser)

        val userDTO = userService.update(1, updatedUserRequest, UserRole.USER, updatedUser.email)

        assertEquals(expectedModifiedUser, userDTO)
    }

    @Test
    fun `when an user with admin permission tries to modify any account should return the modified user`() {
        // given
        every { DateTime.now() }.returns(actualDateTime)

        every { jwtService.sign(updatedAdminUser.email, updatedAdminUser.role) }.returns("token_test")

        every { userRepositoryMock.get(1) }.returns(expectedUser)
        every { userRepositoryMock.update(1, updatedAdminUser) }.returns(expectedAdminModifiedUser)

        // when
        val userDTO = userService.update(1, updatedAdminUserRequest, UserRole.ADMIN, updatedAdminUser.email + "A")

        // then
        assertEquals(expectedAdminModifiedUser, userDTO)
    }

    @Test(expected = UnauthorizedDifferentUserChangeException::class)
    fun `when an user without admin permission tries to modify another account should expect UnauthorizedDifferentUserChangeException`() {
        // given

        every { DateTime.now() }.returns(actualDateTime)

        every { jwtService.sign(updatedAdminUser.email, updatedAdminUser.role) }.returns("token_test")

        every { userRepositoryMock.get(1) }.returns(expectedUser)

        // when
        val userDTO = userService.update(1, updatedAdminUserRequest, UserRole.USER, updatedAdminUser.email + "A")

        // then
        assertEquals(expectedAdminModifiedUser, userDTO)
    }

    @Test(expected = UnauthorizedRoleChangeException::class)
    fun `when an user without admin permission tries to modify its account changing from user to admin should expect UnauthorizedRoleChangeException`() {
        // given

        every { DateTime.now() }.returns(actualDateTime)

        every { jwtService.sign(updatedAdminUser.email, updatedAdminUser.role) }.returns("token_test")

        every { userRepositoryMock.get(1) }.returns(expectedUser)

        // when
        val userDTO = userService.update(1, updatedAdminUserRequest, UserRole.USER, updatedAdminUser.email)

        // then
        assertEquals(expectedAdminModifiedUser, userDTO)
    }

    @Test(expected = EmailAlreadyExistsException::class)
    fun `when an user without admin permission tries to modify its email by another email used, should expect EmailAlreadyExistsException`() {
        // given
        val usedEmail = expectedUser.email + "A"

        val usedEmailUser = expectedUser.copy(email = usedEmail)

        val usedEmailRequest = expectedUserRequest.copy(email = usedEmail)

        every { DateTime.now() }.returns(actualDateTime)

        every { userRepositoryMock.get(1) }.returns(expectedUser)

        every { jwtService.sign(usedEmail, updatedUser.role) }.returns("token_test")

        every { userRepositoryMock.update(1, usedEmailUser) }.throws(EmailAlreadyExistsException())

        // when
        userService.update(1, usedEmailRequest, UserRole.USER, expectedUser.email)

        // then EmailAlreadyExistsException
    }

    @Test
    fun `when a user with valid id was requested, return it`() {
        val expectedUserDTO = UserFactory.sampleDTO(id = 56415)

        every { userRepositoryMock.get(56415) }.returns(expectedUserDTO)
        assertEquals(expectedUserDTO, userService.get(56415))
    }

    @Test(expected = UserNotFoundException::class)
    fun `when a user with invalid id was requested, throws UserNotFoundException`() {
        val userException = UserNotFoundException()
        every { userRepositoryMock.get(844) }.throws(userException)

        userService.get(844)
    }

    @Test(expected = UserNotFoundException::class)
    fun `when a user with invalid id was updated, throws UserNotFoundException`() {
        val userException = UserNotFoundException()
        every { userRepositoryMock.get(844) }.throws(userException)

        userService.update(844, expectedModifiedUserRequest, UserRole.ADMIN, expectedModifiedUser.email)
    }

    @Test(expected = UserNotFoundException::class)
    fun `when a user with invalid id was deleted, throws UserNotFoundException`() {
        val userException = UserNotFoundException()
        every { userRepositoryMock.get(844) }.throws(userException)

        userService.delete(844, UserRole.ADMIN, expectedModifiedUser.email)
    }

    @Test
    fun `when an user tries login with an invalid email, should expect a Validation exception with field email=invalid email`() {
        every { validatorRequest.validate(invalidUserLogin) } throws ValidationException(hashMapOf("email: myemail.com" to mutableListOf("please fill with a valid email")))
        expectedEx.expect(ValidationException::class.java)
        expectedEx.expectMessage("The constraintValidator does not successful in following field(s): {email: myemail.com=[please fill with a valid email]}")
        userService.login(invalidUserLogin)
    }

    @Test
    fun `when an user tries login with a valid email and password and them matches with a user, return the signed user`() {
        // given validUserLogin

        // when
        every { userRepositoryMock.findByEmail(validUserLogin.email!!) }.returns(Optional.of(expectedUser))
        every { jwtService.sign(validUserLogin.email!!, UserRole.USER) }.returns("token_updated")
        val updatedTokenUser = expectedUser.copy(token = "token_updated")

        every { passwordServiceMock.verify(validUserLogin.password!!, expectedUser.password) } returns true
        every { userRepositoryMock.update(expectedUser.id!!, updatedTokenUser) }.returns(updatedTokenUser)
        every { userRepositoryMock.get(expectedUser.id!!) }.returns(updatedTokenUser)

        val userDTO = userService.login(validUserLogin)

        // then
        assertEquals(updatedTokenUser, userDTO)
    }

    @Test
    fun `when an user tries login with default admin, should verify the password without encryption too`() {
        // given adminUserLogin
        val adminEmail = "admin@itimalia.org"
        val adminUserLogin = UserLoginRequest(adminEmail, "admin")

        // when
        every { userRepositoryMock.findByEmail(adminUserLogin.email!!) }.returns(
            Optional.of(expectedAdminModifiedUser.copy(email = adminEmail, password = "admin"))
        )
        every { jwtService.sign(validUserLogin.email!!, UserRole.ADMIN) }.returns("token_updated")
        val updatedTokenUser = expectedAdminModifiedUser.copy(token = "token_updated")

        every { passwordServiceMock.verify(adminUserLogin.password!!, any()) } returns false
        every { userRepositoryMock.update(expectedAdminModifiedUser.id!!, updatedTokenUser) }.returns(updatedTokenUser)
        every { userRepositoryMock.get(expectedAdminModifiedUser.id!!) }.returns(updatedTokenUser)

        val userDTO = userService.login(adminUserLogin)

        // then
        assertEquals(updatedTokenUser, userDTO)
    }

    @Test
    fun `when an user find by email and it is correct, return the user`() {
        // given validUserLogin

        // when
        every { userRepositoryMock.findByEmail(validUserLogin.email!!) }.returns(Optional.of(expectedUser))

        val userDTO = userService.findByEmail(validUserLogin.email!!)

        // then
        assertEquals(expectedUser, userDTO)
    }

    @Test(expected = UserNotFoundException::class)
    fun `when an user tries login with a valid email but not matches with any user, should expect UserNotFoundException`() {
        // given validUserLogin

        // when
        every { userRepositoryMock.findByEmail(validUserLogin.email!!) }.returns(Optional.empty())
        userService.login(validUserLogin)

        // then UserNotFoundException
    }

    @Test(expected = UserNotFoundException::class)
    fun `when an user tries login with a valid email, matches one user, but password not, should expect UserNotFoundException`() {
        // given validUserLogin

        // when
        every { userRepositoryMock.findByEmail(validUserLogin.email!!) }.returns(Optional.of(expectedUser))
        every { passwordServiceMock.verify(validUserLogin.password!!, expectedUser.password) } returns false
        userService.login(validUserLogin)

        // then UserNotFoundException
    }

    @Test
    fun `when an user without admin permission tries to delete your account should return no content with status 204`() {
        every { userRepositoryMock.get(1) }.returns(expectedUser)
        userService.delete(1, UserRole.USER, expectedUser.email)
        verify { userRepositoryMock.delete(1) }
    }

    @Test
    fun `when an user with admin permission tries to delete any account should return no content with status 204`() {
        userService.delete(1, UserRole.ADMIN, "admin@itimalia.org")
        verify { userRepositoryMock.delete(1) }
    }

    @Test(expected = UnauthorizedDifferentUserChangeException::class)
    fun `when an user without admin permission tries to delete another account should expect UnauthorizedDifferentUserChangeException`() {
        every { userRepositoryMock.get(1) }.returns(expectedUser)

        userService.delete(1, expectedUser.role, expectedUser.email + "A")
    }
}
