package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.Gender
import com.abrigo.itimalia.domain.entities.Roles
import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.UserDTO
import com.abrigo.itimalia.domain.entities.user.UserLogin
import com.abrigo.itimalia.domain.exceptions.*
import com.abrigo.itimalia.domain.jwt.JWTUtils
import com.abrigo.itimalia.domain.repositories.UserRepository
import com.abrigo.itimalia.domain.repositories.factories.UserFactory
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
    private lateinit var newUserDTO: UserDTO
    private lateinit var expectedUserDTO: UserDTO
    private lateinit var expectedAdminModifiedUserDTO: UserDTO
    private lateinit var birthDate: DateTime
    private lateinit var jwtUtils: JWTUtils
    private lateinit var actualDateTime: DateTime
    private lateinit var invalidUserLogin: UserLogin
    private lateinit var updatedAdminUserDTO: UserDTO
    private lateinit var expectedModifiedUserDTO: UserDTO
    private lateinit var validUserLogin: UserLogin
    private lateinit var updatedUserDTO: UserDTO
    private lateinit var userService: UserService

    @get:Rule
    val expectedEx = ExpectedException.none()

    @Before
    fun setup() {
        val formatter = DateTimeFormat.forPattern("dd/mm/yyyy")
        birthDate=formatter.parseDateTime("01/01/1990")
        mockkStatic(DateTime::class)
        actualDateTime= DateTime.now()

        newUserDTO = UserFactory.sampleDTO(id = null, birthDate = birthDate, creationDate = actualDateTime, modificationDate = actualDateTime)
        expectedUserDTO = newUserDTO.copy(id = 1, token = "token_test")
        expectedModifiedUserDTO = expectedUserDTO.copy(password = expectedUserDTO.password+"123")
        updatedUserDTO = expectedUserDTO.copy(password = expectedUserDTO.password+"123")
        expectedAdminModifiedUserDTO = expectedUserDTO.copy(role = Roles.ADMIN)
        updatedAdminUserDTO=expectedUserDTO.copy(role = Roles.ADMIN)

        invalidUserLogin= UserFactory.sampleLogin(email = "myemail.com")

        validUserLogin= UserFactory.sampleLogin()

        userRepositoryMock= mockk(relaxed = true)

        jwtUtils= mockk(relaxed = true)

        userService = UserServiceImpl(userRepositoryMock, jwtUtils)
    }


    @Test
    fun `when a valid user without admin permissions request a sign up, register it`(){
        val user = UserFactory.sampleNew(birthDate = birthDate)
        every { DateTime.now() }.returns(actualDateTime)

        every { userRepositoryMock.findByEmail(newUserDTO.email) }.throws(UserNotFoundException())

        every { jwtUtils.sign(newUserDTO.email, Roles.USER, 5) }.returns("token_test")

        every { userRepositoryMock.add(newUserDTO.copy(role = Roles.USER, token = "token_test"))  }.returns(expectedUserDTO)

        val userDTO=userService.add(user)

        assertEquals(expectedUserDTO,userDTO)
    }

    @Test(expected = EmailAlreadyExistsException::class)
    fun `when a valid user request a sign up but the email already exists, should expect EmailAlreadyExistsException`(){
        val user = NewUser(
            "newUser@com.abrigo.itimalia.domain.com",
            "password",
            birthDate,
            Gender.MASC,
            "New User",
            "81823183183"
        )

        val unexpectedUserDTO = expectedUserDTO

        every { DateTime.now() }.returns(actualDateTime)

        every { userRepositoryMock.findByEmail(newUserDTO.email) }.returns(unexpectedUserDTO)

        userService.add(user)
    }

    @Test
    fun `when an user without admin permission tries to modify its account should return the modified user`(){

        every { DateTime.now() }.returns(actualDateTime)

        every { jwtUtils.sign(updatedUserDTO.email, updatedUserDTO.role, 5) }.returns("token_test")

        every { userRepositoryMock.get(1)}.returns(expectedUserDTO)
        every { userRepositoryMock.findByEmail(updatedUserDTO.email) }.throws(UserNotFoundException())

        every { userRepositoryMock.update(1,updatedUserDTO) }.returns(expectedModifiedUserDTO)


        val userDTO=UserServiceImpl(userRepositoryMock,jwtUtils).update(1,updatedUserDTO, Roles.USER, updatedUserDTO.email)

        assertEquals(expectedModifiedUserDTO,userDTO)
    }

    @Test
    fun `when an user without admin permission tries to modify its account without change the email should return the modified user`(){

        every { DateTime.now() }.returns(actualDateTime)

        every { jwtUtils.sign(updatedUserDTO.email, updatedUserDTO.role, 5) }.returns("token_test")

        every { userRepositoryMock.get(1)}.returns(expectedUserDTO)
        every { userRepositoryMock.findByEmail(updatedUserDTO.email) }.returns(expectedUserDTO)

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
        every { userRepositoryMock.findByEmail(updatedAdminUserDTO.email) }.throws(UserNotFoundException())

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
        every { userRepositoryMock.findByEmail(updatedAdminUserDTO.email) }.throws(UserNotFoundException())


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
        every { userRepositoryMock.findByEmail(expectedUserDTO.email) }.throws(UserNotFoundException())

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
        every { userRepositoryMock.findByEmail(expectedUserDTO.email) }.throws(UserNotFoundException())

        //when
        UserServiceImpl(userRepositoryMock,jwtUtils).update(1,expectedUserDTO, Roles.USER, expectedUserDTO.email)

        //then expect UnmodifiedUserException
    }

    @Test(expected = EmailAlreadyExistsException::class)
    fun `when an user without admin permission tries to modify its email by another email used, should expect EmailAlreadyExistsException`(){
        //given

        every { DateTime.now() }.returns(actualDateTime)

        every { userRepositoryMock.get(1)}.returns(expectedUserDTO)

        val usedEmailUser = expectedUserDTO.copy(id = 2, email = expectedUserDTO.email+"A")
        every { userRepositoryMock.findByEmail(expectedUserDTO.email) }.returns(usedEmailUser)

        val modifiedUserDTO = expectedUserDTO.copy(email = expectedUserDTO.email+"A")

        //when
        userService.update(1,modifiedUserDTO, Roles.USER, expectedUserDTO.email)

        //then EmailAlreadyExistsException
    }

    @Test(expected = UnmodifiedUserException::class)
    fun `when an user with admin permission tries to modify its account but without modifications should expect UnmodifiedUserException`(){
        //given

        every { DateTime.now() }.returns(actualDateTime)

        every { jwtUtils.sign(expectedAdminModifiedUserDTO.email, expectedAdminModifiedUserDTO.role, 5) }.returns("token_test")

        every { userRepositoryMock.get(1)}.returns(expectedAdminModifiedUserDTO)
        every { userRepositoryMock.findByEmail(updatedAdminUserDTO.email) }.throws(UserNotFoundException())

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
        every { userRepositoryMock.findByEmail(updatedAdminUserDTO.email) }.throws(UserNotFoundException())

        //when
        UserServiceImpl(userRepositoryMock,jwtUtils).update(1,expectedUserDTO, Roles.ADMIN, updatedAdminUserDTO.email)

        //then return exception
    }

    @Test
    fun `when a user without admin permissions with invalid gender tries sign in, expect ValidationException with fields gender = invalid gender`(){
        val newUser = NewUser(
            "newUser@com.abrigo.itimalia.domain.com",
            "password",
            birthDate,
            null,
            "New User",
            "81823183183"
        )

        every { userRepositoryMock.findByEmail(newUser.email) }.throws(UserNotFoundException())

        expectedEx.expect(ValidationException::class.java)
        expectedEx.expectMessage("The validation does not successful in following field(s): {gender=[invalid gender]}")

        userService.add(newUser)
    }

    @Test
    fun `when a user without admin permissions with invalid birth date tries sign in, expect ValidationException with fields birthdate = invalid birthDate`(){
        val newUser = NewUser(
            "newUser@com.abrigo.itimalia.domain.com",
            "password",
            null,
            Gender.MASC,
            "New User",
            "81823183183"
        )
        every { userRepositoryMock.findByEmail(newUser.email) }.throws(UserNotFoundException())

        expectedEx.expect(ValidationException::class.java)
        expectedEx.expectMessage("The validation does not successful in following field(s): {birthDate=[invalid birthDate]}")
        userService.add(newUser)
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

        userService.update(844, expectedModifiedUserDTO, Roles.ADMIN, expectedModifiedUserDTO.email)
    }

    @Test(expected = UserNotFoundException::class)
    fun `when a user with invalid id was deleted, throws UserNotFoundException`(){
        val userException=UserNotFoundException()
        every { userRepositoryMock.get(844)  }.throws(userException)

        userService.delete(844, Roles.ADMIN, expectedModifiedUserDTO.email)
    }

    @Test
    fun `when an user tries login with an invalid email, should expect a Validation exception with field email=invalid email`() {
        expectedEx.expect(ValidationException::class.java)
        expectedEx.expectMessage("The validation does not successful in following field(s): {email=[invalid email]}")
        userService.login(invalidUserLogin)
    }

    @Test
    fun `when an user tries login with a valid email and password and them matches with a user, return the signed user`() {
        //given validUserLogin

        //when
        every { userRepositoryMock.findByCredentials(validUserLogin.email, validUserLogin.password) }.returns(expectedUserDTO)
        every { jwtUtils.sign(validUserLogin.email, Roles.USER, 5) }.returns("token_updated")
        val updatedTokenUser = expectedUserDTO.copy(token = "token_updated")
        every { userRepositoryMock.update(expectedUserDTO.id!!, updatedTokenUser) }.returns(updatedTokenUser)
        every { userRepositoryMock.get(expectedUserDTO.id!!) }.returns(updatedTokenUser)

        val userDTO=userService.login(validUserLogin)

        //then
        assertEquals(updatedTokenUser,userDTO)
    }

    @Test(expected = UserNotFoundException::class)
    fun `when an user tries login with a valid email and password but not matches with any user, should expect UserNotFoundException`() {
        //given validUserLogin

        //when
        every { userRepositoryMock.findByCredentials(validUserLogin.email, validUserLogin.password) }.throws(UserNotFoundException())
        userService.login(validUserLogin)

        //then UserNotFoundException
    }

    @Test
    fun `when an user without admin permission tries to delete your account should return no content with status 204`(){

        every { userRepositoryMock.get(1) }.returns(expectedUserDTO)
        userService.delete(1, Roles.USER, expectedUserDTO.email)
        verify { userRepositoryMock.delete(1) }
    }

    @Test
    fun `when an user with admin permission tries to delete any account should return no content with status 204`(){
        userService.delete(1, Roles.ADMIN, "admin@itimalia.org")
        verify { userRepositoryMock.delete(1) }
    }

    @Test(expected = UnauthorizedDifferentUserChangeException::class)
    fun `when an user without admin permission tries to delete another account should expect UnauthorizedDifferentUserChangeException`(){
        every { userRepositoryMock.get(1) }.returns(expectedUserDTO)

        UserServiceImpl(userRepositoryMock,jwtUtils).delete(1, expectedUserDTO.role, expectedUserDTO.email+"A")
    }


}

