package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.user.Gender
import com.abrigo.itimalia.domain.entities.user.NewUserRequest
import com.abrigo.itimalia.domain.entities.user.User
import com.abrigo.itimalia.domain.entities.user.UserLoginRequest
import com.abrigo.itimalia.domain.entities.user.UserRequest
import com.abrigo.itimalia.domain.entities.user.UserRole
import com.abrigo.itimalia.domain.entities.user.toUser
import com.abrigo.itimalia.domain.exceptions.EmailAlreadyExistsException
import com.abrigo.itimalia.domain.exceptions.UnauthorizedDifferentUserChangeException
import com.abrigo.itimalia.domain.exceptions.UnauthorizedRoleChangeException
import com.abrigo.itimalia.domain.exceptions.UnmodifiedUserException
import com.abrigo.itimalia.domain.exceptions.UserNotFoundException
import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.domain.jwt.JWTService
import com.abrigo.itimalia.domain.repositories.UserRepository
import com.abrigo.itimalia.domain.validation.Validator
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
import kotlin.test.assertEquals


class UserServiceTest{

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
    private lateinit var validatorNewUser: Validator<NewUserRequest>
    private lateinit var validatorUser: Validator<UserRequest>
    private lateinit var validatorUserLogin: Validator<UserLoginRequest>

    private val defaultToken = "default_token"

    @get:Rule
    val expectedEx = ExpectedException.none()

    @Before
    fun setup() {

        validatorNewUser = mockk(relaxed = true)
        validatorUser = mockk(relaxed = true)
        validatorUserLogin = mockk(relaxed = true)
        val formatter = DateTimeFormat.forPattern("dd/mm/yyyy")
        birthDate=formatter.parseDateTime("01/01/1990")
        mockkStatic(DateTime::class)
        actualDateTime= DateTime.now()

        newUserRequest = UserFactory.sampleDTORequest(id = null, birthDate = birthDate, creationDate = actualDateTime, modificationDate = actualDateTime)
        newUser = newUserRequest.toUser()
        expectedUserRequest = newUserRequest.copy(id = 1, token = "token_test")
        expectedUser = expectedUserRequest.toUser()
        updatedUserRequest = expectedUserRequest.copy(password = expectedUser.password+"123")
        updatedUser = updatedUserRequest.toUser()
        expectedAdminModifiedUserRequest = expectedUserRequest.copy(role = UserRole.ADMIN)
        expectedAdminModifiedUser = expectedAdminModifiedUserRequest.toUser()
        expectedModifiedUserRequest = expectedUserRequest.copy(password = expectedUserRequest.password+"123")
        expectedModifiedUser = expectedUserRequest.toUser()

        updatedAdminUserRequest = expectedUserRequest.copy(role = UserRole.ADMIN)
        updatedAdminUser=updatedAdminUserRequest.toUser()

        invalidUserLogin= UserFactory.sampleLoginRequest(email = "myemail.com")

        validUserLogin= UserFactory.sampleLoginRequest()



        userRepositoryMock= mockk(relaxed = true)

        jwtService= mockk(relaxed = true)

        userService = UserServiceImpl(userRepositoryMock, jwtService, validatorNewUser, validatorUser, validatorUserLogin)
    }


    @Test
    fun `when a valid user without admin permissions request a sign up, register it`(){
        val userRequest = UserFactory.sampleNewRequest(birthDate = birthDate)
        every { DateTime.now() }.returns(actualDateTime)

        every { userRepositoryMock.findByEmail(newUser.email) }.throws(UserNotFoundException())

        every { jwtService.sign(newUser.email, UserRole.USER) }.returns("token_test")

        every { userRepositoryMock.add(newUser.copy(role = UserRole.USER, token = "token_test"))  }.returns(expectedUser)

        val userDTO=userService.add(userRequest)

        assertEquals(expectedUser,userDTO)
    }

    @Test(expected = EmailAlreadyExistsException::class)
    fun `when a valid user request a sign up but the email already exists, should expect EmailAlreadyExistsException`(){
        val newUserRequest = NewUserRequest(
            "newUser@com.abrigo.itimalia.domain.com",
            "password",
            birthDate,
            Gender.MALE,
            "New User",
            "81823183183"
        )

        val unexpectedUserDTO = expectedUser

        every { DateTime.now() }.returns(actualDateTime)

        every { userRepositoryMock.findByEmail(newUser.email) }.returns(unexpectedUserDTO)

        userService.add(newUserRequest)
    }

    @Test
    fun `when an user without admin permission tries to modify its account should return the modified user`(){

        every { DateTime.now() }.returns(actualDateTime)

        every { jwtService.sign(updatedUser.email, updatedUser.role) }.returns("token_test")

        every { userRepositoryMock.get(1)}.returns(expectedUser)
        every { userRepositoryMock.findByEmail(updatedUser.email) }.throws(UserNotFoundException())

        every { userRepositoryMock.update(1,updatedUser) }.returns(expectedModifiedUser)


        val userDTO= userService.update(1,updatedUserRequest, UserRole.USER, updatedUser.email)

        assertEquals(expectedModifiedUser,userDTO)
    }

    @Test
    fun `when an user without admin permission tries to modify its account without change the email should return the modified user`(){

        every { DateTime.now() }.returns(actualDateTime)

        every { jwtService.sign(updatedUser.email, updatedUser.role) }.returns("token_test")

        every { userRepositoryMock.get(1)}.returns(expectedUser)
        every { userRepositoryMock.findByEmail(updatedUser.email) }.returns(expectedUser)

        every { userRepositoryMock.update(1,updatedUser) }.returns(expectedModifiedUser)


        val userDTO=userService.update(1,updatedUserRequest, UserRole.USER, updatedUser.email)

        assertEquals(expectedModifiedUser,userDTO)
    }

    @Test
    fun `when an user with admin permission tries to modify any account should return the modified user`(){

        //given

        every { DateTime.now() }.returns(actualDateTime)

        every { jwtService.sign(updatedAdminUser.email, updatedAdminUser.role) }.returns("token_test")

        every { userRepositoryMock.get(1)}.returns(expectedUser)
        every { userRepositoryMock.update(1,updatedAdminUser) }.returns(expectedAdminModifiedUser)
        every { userRepositoryMock.findByEmail(updatedAdminUser.email) }.throws(UserNotFoundException())

        //when
        val userDTO=userService.update(1,updatedAdminUserRequest, UserRole.ADMIN, updatedAdminUser.email+"A")

        //then
        assertEquals(expectedAdminModifiedUser,userDTO)
    }

    @Test(expected = UnauthorizedDifferentUserChangeException::class)
    fun `when an user without admin permission tries to modify another account should expect UnauthorizedDifferentUserChangeException`(){
        //given

        every { DateTime.now() }.returns(actualDateTime)

        every { jwtService.sign(updatedAdminUser.email, updatedAdminUser.role) }.returns("token_test")

        every { userRepositoryMock.get(1)}.returns(expectedUser)
        every { userRepositoryMock.findByEmail(updatedAdminUser.email) }.throws(UserNotFoundException())


        //when
        val userDTO=userService.update(1,updatedAdminUserRequest, UserRole.USER, updatedAdminUser.email+"A")

        //then
        assertEquals(expectedAdminModifiedUser,userDTO)
    }

    @Test(expected = UnauthorizedRoleChangeException::class)
    fun `when an user without admin permission tries to modify its account changing from user to admin should expect UnauthorizedRoleChangeException`(){
        //given

        every { DateTime.now() }.returns(actualDateTime)

        every { jwtService.sign(updatedAdminUser.email, updatedAdminUser.role) }.returns("token_test")

        every { userRepositoryMock.get(1)}.returns(expectedUser)
        every { userRepositoryMock.findByEmail(expectedUser.email) }.throws(UserNotFoundException())

        //every { userRepositoryMock.update(1,updatedAdminUserDTO) }.returns(expectedAdminModifiedUserDTO)


        //when
        val userDTO= userService.update(1,updatedAdminUserRequest, UserRole.USER, updatedAdminUser.email)

        //then
        assertEquals(expectedAdminModifiedUser,userDTO)
    }

    @Test(expected = UnmodifiedUserException::class)
    fun `when an user without admin permission tries to modify its account but without modifications should expect UnmodifiedUserException`(){
        //given

        every { DateTime.now() }.returns(actualDateTime)

        every { jwtService.sign(expectedUser.email, expectedUser.role) }.returns("token_test")

        every { userRepositoryMock.get(1)}.returns(expectedUser)
        every { userRepositoryMock.findByEmail(expectedUser.email) }.throws(UserNotFoundException())

        //when
        userService.update(1,expectedUserRequest, UserRole.USER, expectedUser.email)

        //then expect UnmodifiedUserException
    }

    @Test(expected = EmailAlreadyExistsException::class)
    fun `when an user without admin permission tries to modify its email by another email used, should expect EmailAlreadyExistsException`(){
        //given

        every { DateTime.now() }.returns(actualDateTime)

        every { userRepositoryMock.get(1)}.returns(expectedUser)

        val usedEmailUser = expectedUser.copy(id = 2, email = expectedUser.email+"A")
        every { userRepositoryMock.findByEmail(expectedUser.email) }.returns(usedEmailUser)

        val modifiedUserDTO = expectedUserRequest.copy(email = expectedUser.email+"A")

        //when
        userService.update(1,modifiedUserDTO, UserRole.USER, expectedUser.email)

        //then EmailAlreadyExistsException
    }

    @Test(expected = UnmodifiedUserException::class)
    fun `when an user with admin permission tries to modify its account but without modifications should expect UnmodifiedUserException`(){
        //given

        every { DateTime.now() }.returns(actualDateTime)

        every { jwtService.sign(expectedAdminModifiedUser.email, expectedAdminModifiedUser.role) }.returns("token_test")

        every { userRepositoryMock.get(1)}.returns(expectedAdminModifiedUser)
        every { userRepositoryMock.findByEmail(updatedAdminUser.email) }.throws(UserNotFoundException())

        //when
        userService.update(1,expectedAdminModifiedUserRequest, UserRole.ADMIN, expectedAdminModifiedUser.email)

        //then
        //assertEquals(expectedAdminModifiedUserDTO,userDTO)
    }

    @Test(expected = UnmodifiedUserException::class)
    fun `when an user with admin permission tries to modify another account but without modifications should expect UnmodifiedUserException`(){
        //given

        every { DateTime.now() }.returns(actualDateTime)

        //every { jwtService.sign(expectedAdminModifiedUserDTO.email, expectedAdminModifiedUserDTO.role, 5) }.returns("token_test")

        every { userRepositoryMock.get(1)}.returns(expectedUser)
        //every { userRepositoryMock.update(1,updatedAdminUserDTO) }.returns(expectedAdminModifiedUserDTO)
        every { userRepositoryMock.findByEmail(updatedAdminUser.email) }.throws(UserNotFoundException())

        //when
        userService.update(1,expectedUserRequest, UserRole.ADMIN, updatedAdminUser.email)

        //then return exception
    }

    @Test
    fun `when a user with valid id was requested, return it`(){
        val expectedUserDTO = UserFactory.sampleDTO(id = 56415)

        every { userRepositoryMock.get(56415)  }.returns(expectedUserDTO)
        assertEquals(expectedUserDTO, userService.get(56415))
    }


    @Test(expected = UserNotFoundException::class)
    fun `when a user with invalid id was requested, throws UserNotFoundException`(){
        val userException=UserNotFoundException()
        every { userRepositoryMock.get(844)  }.throws(userException)

        userService.get(844)
    }

    @Test(expected = UserNotFoundException::class)
    fun `when a user with invalid id was updated, throws UserNotFoundException`(){
        val userException=UserNotFoundException()
        every { userRepositoryMock.get(844)  }.throws(userException)

        userService.update(844, expectedModifiedUserRequest, UserRole.ADMIN, expectedModifiedUser.email)
    }

    @Test(expected = UserNotFoundException::class)
    fun `when a user with invalid id was deleted, throws UserNotFoundException`(){
        val userException=UserNotFoundException()
        every { userRepositoryMock.get(844)  }.throws(userException)

        userService.delete(844, UserRole.ADMIN, expectedModifiedUser.email)
    }

    @Test
    fun `when an user tries login with an invalid email, should expect a Validation exception with field email=invalid email`() {
        every { validatorUserLogin.validate(invalidUserLogin) } throws ValidationException(hashMapOf("email: myemail.com" to mutableListOf("please fill with a valid email")))
        expectedEx.expect(ValidationException::class.java)
        expectedEx.expectMessage("The constraintValidator does not successful in following field(s): {email: myemail.com=[please fill with a valid email]}")
        userService.login(invalidUserLogin)
    }

    @Test
    fun `when an user tries login with a valid email and password and them matches with a user, return the signed user`() {
        //given validUserLogin

        //when
        every { userRepositoryMock.findByCredentials(validUserLogin.email!!, validUserLogin.password!!) }.returns(expectedUser)
        every { jwtService.sign(validUserLogin.email!!, UserRole.USER) }.returns("token_updated")
        val updatedTokenUser = expectedUser.copy(token = "token_updated")
        every { userRepositoryMock.update(expectedUser.id!!, updatedTokenUser) }.returns(updatedTokenUser)
        every { userRepositoryMock.get(expectedUser.id!!) }.returns(updatedTokenUser)

        val userDTO=userService.login(validUserLogin)

        //then
        assertEquals(updatedTokenUser,userDTO)
    }

    @Test(expected = UserNotFoundException::class)
    fun `when an user tries login with a valid email and password but not matches with any user, should expect UserNotFoundException`() {
        //given validUserLogin

        //when
        every { userRepositoryMock.findByCredentials(validUserLogin.email!!, validUserLogin.password!!) }.throws(UserNotFoundException())
        userService.login(validUserLogin)

        //then UserNotFoundException
    }

    @Test
    fun `when an user without admin permission tries to delete your account should return no content with status 204`(){

        every { userRepositoryMock.get(1) }.returns(expectedUser)
        userService.delete(1, UserRole.USER, expectedUser.email)
        verify { userRepositoryMock.delete(1) }
    }

    @Test
    fun `when an user with admin permission tries to delete any account should return no content with status 204`(){
        userService.delete(1, UserRole.ADMIN, "admin@itimalia.org")
        verify { userRepositoryMock.delete(1) }
    }

    @Test(expected = UnauthorizedDifferentUserChangeException::class)
    fun `when an user without admin permission tries to delete another account should expect UnauthorizedDifferentUserChangeException`(){
        every { userRepositoryMock.get(1) }.returns(expectedUser)

        userService.delete(1, expectedUser.role, expectedUser.email+"A")
    }
}

