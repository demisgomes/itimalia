package application.web.controllers

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import domain.entities.*
import domain.exceptions.*
import domain.services.UserService
import io.javalin.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.eclipse.jetty.http.HttpStatus
import org.junit.Before
import org.junit.Test
import java.text.DateFormat
import java.text.SimpleDateFormat

class UserControllerTest{
    lateinit var userServiceMock: UserService
    lateinit var contextMock: Context
    lateinit var returnedUser:UserDTO
    lateinit var returnedAdminUser:UserDTO
    lateinit var newUser:NewUser
    lateinit var newAdminUser:NewUser
    lateinit var newLoginUser: UserLogin

    @Before
    fun setup(){
        userServiceMock = mockk(relaxed=true)
        contextMock = mockk(relaxed = true)

        val formatter: DateFormat = SimpleDateFormat("dd/mm/yyyy")
        val date=formatter.parse("01/01/1990")
        returnedUser= UserDTO(
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
            null)

        returnedAdminUser= UserDTO(
            null,
            "newUser@domain.com",
            "password",
            date,
            Gender.MASC,
            "New User",
            "81823183183",
            Roles.ADMIN,
            null,
            null,
            null)

        newUser = NewUser(
            "newUser@domain.com",
            "password",
            date,
            Gender.MASC,
            "New User",
            "81823183183",
            Roles.USER
        )

        newAdminUser = NewUser(
            "newUser@domain.com",
            "password",
            date,
            Gender.MASC,
            "New User",
            "81823183183",
            Roles.ADMIN
        )

        newLoginUser = UserLogin(
            "newUser@domain.com",
            "password"
        )
    }

    @Test
    fun `when request a user with a valid id, return it`(){
        every{ userServiceMock.get(1)}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        UserController(userServiceMock).findUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when request a user with an invalid id, return not found`(){
        val userNotFoundException=UserNotFoundException()
        every{ userServiceMock.get(1)}.throws(userNotFoundException)

        every { contextMock.pathParam("id") }.returns("1")

        UserController(userServiceMock).findUser(contextMock)

        verify { contextMock.json(userNotFoundException.createErrorResponse()).status(HttpStatus.NOT_FOUND_404) }
    }

    @Test
    fun `when add a valid user should return the user with status 201`(){
        every{ userServiceMock.add(newUser)}.returns(returnedUser)

        every { contextMock.body<NewUser>() }.returns(newUser)

        UserController(userServiceMock).addUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.CREATED_201) }
    }

    @Test
    fun `when add a invalid user with less than 13 years old should return validation error with status 400`(){
        val validationException=ValidationException(hashMapOf("birthDate" to mutableListOf("Only accept users with 13 years old or more")))

        every{ userServiceMock.add(newUser)}.throws(validationException)

        every { contextMock.body<NewUser>() }.returns(newUser)

        UserController(userServiceMock).addUser(contextMock)

        verify { contextMock.json(validationException.createErrorResponse()).status(HttpStatus.BAD_REQUEST_400) }
    }


    @Test
    fun `when modify a valid user should return the user modified with status 200`(){
        every{ userServiceMock.update(1, returnedUser)}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock).updateUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when modify a valid user but without modifications should return an unmodified exception with status 406`(){
        val unmodifiedUserException=UnmodifiedUserException()
        every{ userServiceMock.update(1, returnedUser)}.throws(unmodifiedUserException)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock).updateUser(contextMock)

        verify { contextMock.json(unmodifiedUserException.createErrorResponse()).status(HttpStatus.NOT_ACCEPTABLE_406) }
    }

    @Test
    fun `when modify a valid user but with a non-existent id should return an user not found exception with status 404`(){
        val userNotFoundException=UserNotFoundException()
        every{ userServiceMock.update(1, returnedUser)}.throws(userNotFoundException)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock).updateUser(contextMock)

        verify { contextMock.json(userNotFoundException.createErrorResponse()).status(HttpStatus.NOT_FOUND_404) }
    }


    @Test
    fun `when modify a invalid user that exists should return a validation exception with status 400`(){
        val validationException=ValidationException(hashMapOf("birthDate" to mutableListOf("Only accept users with 13 years old or more")))
        every{ userServiceMock.update(1, returnedUser)}.throws(validationException)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock).updateUser(contextMock)

        verify { contextMock.json(validationException.createErrorResponse()).status(HttpStatus.BAD_REQUEST_400) }
    }

    @Test
    fun `when delete a user by id and it exists, return 200 with user deleted`(){
        every{ userServiceMock.delete(1)}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock).deleteUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when delete a user by id and it does not exists, should return not found with status 404 `(){
        val userNotFoundException=UserNotFoundException()
        every{ userServiceMock.delete(1)}.throws(userNotFoundException)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock).deleteUser(contextMock)

        verify { contextMock.json(userNotFoundException.createErrorResponse()).status(HttpStatus.NOT_FOUND_404) }
    }
//    @Test
//    fun `when a user has a invalid gender, should return bad request with status 400`(){
//        val invalidGenderException=InvalidGenderException()
//        val invalidFormatException=InvalidFormatException.from(contextMock.body<NewUser>())
//
//        every { contextMock.body<NewUser>() }.throws(InvalidFormatException())
//
//        UserController(userServiceMock).addUser(contextMock)
//
//        verify { contextMock.json(invalidGenderException.createErrorResponse()).status(HttpStatus.BAD_REQUEST_400) }
//    }

    @Test
    fun `when add a valid user with admin permissions should return the user with status 201`(){

        every{ userServiceMock.add(newAdminUser)}.returns(returnedAdminUser)

        every { contextMock.body<NewUser>() }.returns(newAdminUser)

        UserController(userServiceMock).addUser(contextMock)

        verify { contextMock.json(returnedAdminUser).status(HttpStatus.CREATED_201) }
    }

    @Test
    fun `when a user with valid credentials log in, should return the logged user with status 200`(){
        every { userServiceMock.login(newLoginUser) }.returns(returnedUser)

        every { contextMock.body<UserLogin>() }.returns(newLoginUser)

        UserController(userServiceMock).loginUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }

    }
    @Test
    fun `when a user with non matching credentials log in, should return unauthorized with status 401`(){
        val invalidCredentialsException=InvalidCredentialsException()
        every { userServiceMock.login(newLoginUser) }.throws(invalidCredentialsException)

        every { contextMock.body<UserLogin>() }.returns(newLoginUser)

        UserController(userServiceMock).loginUser(contextMock)

        verify { contextMock.json(invalidCredentialsException.createErrorResponse()).status(HttpStatus.UNAUTHORIZED_401) }

    }



}