package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.domain.entities.Roles
import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.UserDTO
import com.abrigo.itimalia.domain.entities.user.UserLogin
import com.abrigo.itimalia.domain.entities.user.UserSearched
import com.abrigo.itimalia.domain.exceptions.*
import com.abrigo.itimalia.domain.jwt.JWTAccessManager
import com.abrigo.itimalia.domain.repositories.factories.UserFactory
import com.abrigo.itimalia.domain.services.UserService
import io.javalin.http.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.eclipse.jetty.http.HttpStatus
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.Before
import org.junit.Test

class UserControllerTest{
    lateinit var userServiceMock: UserService
    lateinit var contextMock: Context
    lateinit var returnedUser: UserDTO
    lateinit var newUser: NewUser
    lateinit var newLoginUser: UserLogin
    lateinit var jwtAccessManagerMock: JWTAccessManager
    lateinit var actualDateTime:DateTime

    @Before
    fun setup(){
        userServiceMock = mockk(relaxed=true)
        contextMock = mockk(relaxed = true)
        jwtAccessManagerMock=mockk(relaxed = true)
        actualDateTime= DateTime.now()
        mockkStatic(UserSearched::class)
        val formatter = DateTimeFormat.forPattern("dd/mm/yyyy")
        val birthDate=formatter.parseDateTime("01/01/1990")
        returnedUser= UserFactory.sampleDTO(birthDate = birthDate, creationDate = actualDateTime, modificationDate = actualDateTime)
        newUser = UserFactory.sampleNew()
        newLoginUser = UserFactory.sampleLogin()
    }

    @Test
    fun `when request a user with a valid id, return it`(){
        every{ userServiceMock.get(1)}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        UserController(userServiceMock, jwtAccessManagerMock).findUser(contextMock)

        verify { contextMock.json(any<UserSearched>()) }
        verify { contextMock.status(HttpStatus.OK_200) }
    }

    @Test
    fun `when request a user with an invalid id, return not found`(){
        val userNotFoundException=UserNotFoundException()
        every{ userServiceMock.get(1)}.throws(userNotFoundException)

        every { contextMock.pathParam("id") }.returns("1")

        UserController(userServiceMock, jwtAccessManagerMock).findUser(contextMock)

        verify { contextMock.json(userNotFoundException.createErrorResponse()).status(HttpStatus.NOT_FOUND_404) }
    }

    @Test
    fun `when add a valid user should return the user with status 201`(){
        every{ userServiceMock.add(newUser)}.returns(returnedUser)

        every { contextMock.body<NewUser>() }.returns(newUser)

        UserController(userServiceMock, jwtAccessManagerMock).addUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.CREATED_201) }
    }

    @Test
    fun `when add a valid user with an existent email should return Email Already Exists with status 401`(){
        val emailAlreadyExistsException=EmailAlreadyExistsException()
        every{ userServiceMock.add(newUser)}.throws(emailAlreadyExistsException)

        every { contextMock.body<NewUser>() }.returns(newUser)

        UserController(userServiceMock, jwtAccessManagerMock).addUser(contextMock)

        verify { contextMock.json(emailAlreadyExistsException.createErrorResponse()).status(HttpStatus.UNAUTHORIZED_401) }
    }

    @Test
    fun `when add a invalid user with less than 13 years old should return validation error with status 400`(){
        val validationException=ValidationException(hashMapOf("birthDate" to mutableListOf("Only accept users with 13 years old or more")))

        every{ userServiceMock.add(newUser)}.throws(validationException)

        every { contextMock.body<NewUser>() }.returns(newUser)

        UserController(userServiceMock, jwtAccessManagerMock).addUser(contextMock)

        verify { contextMock.json(validationException.createErrorResponse()).status(HttpStatus.BAD_REQUEST_400) }
    }


    @Test
    fun `when modify a valid user should return the user modified with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every{ userServiceMock.update(1, returnedUser, returnedUser.role, returnedUser.email)}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).updateUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when modify a valid user but without modifications should return an unmodified exception with status 406`(){
        val unmodifiedUserException=UnmodifiedUserException()
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)



        every{ userServiceMock.update(1, returnedUser, returnedUser.role, returnedUser.email)}.throws(unmodifiedUserException)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).updateUser(contextMock)

        verify { contextMock.json(unmodifiedUserException.createErrorResponse()).status(HttpStatus.NOT_ACCEPTABLE_406) }
    }

    @Test
    fun `when modify a valid user but with a non-existent id should return an user not found exception with status 404`(){
        val userNotFoundException=UserNotFoundException()

        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)


        every{ userServiceMock.update(1, returnedUser, returnedUser.role, returnedUser.email)}.throws(userNotFoundException)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).updateUser(contextMock)

        verify { contextMock.json(userNotFoundException.createErrorResponse()).status(HttpStatus.NOT_FOUND_404) }
    }


    @Test
    fun `when modify a invalid user that exists should return a validation exception with status 400`(){
        val validationException=ValidationException(hashMapOf("birthDate" to mutableListOf("Only accept users with 13 years old or more")))
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every{ userServiceMock.update(1, returnedUser, returnedUser.role, returnedUser.email)}.throws(validationException)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).updateUser(contextMock)

        verify { contextMock.json(validationException.createErrorResponse()).status(HttpStatus.BAD_REQUEST_400) }
    }

    @Test
    fun `when modify an existent user with valid token without admin permissions and different email between modified and modifier should return Unauthorized Different Change User exception with 403`(){
        val unauthorizedDifferentUserChangeException=UnauthorizedDifferentUserChangeException()
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email+"A")

        every{ userServiceMock.update(1, returnedUser, returnedUser.role, returnedUser.email+"A")}.throws(unauthorizedDifferentUserChangeException)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).updateUser(contextMock)

        verify { contextMock.json(unauthorizedDifferentUserChangeException.createErrorResponse()).status(HttpStatus.FORBIDDEN_403) }


    }

    @Test
    fun `when modify an existent user with valid token without admin permissions and equal email between modified and modifier and change its role should return Unauthorized Role Change exception with 403`(){
        val unauthorizedRoleChangeException = UnauthorizedRoleChangeException()
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email+"A")

        every{ userServiceMock.update(1, returnedUser, returnedUser.role, returnedUser.email+"A")}.throws(unauthorizedRoleChangeException)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).updateUser(contextMock)

        verify { contextMock.json(unauthorizedRoleChangeException.createErrorResponse()).status(HttpStatus.FORBIDDEN_403) }


    }

    @Test
    fun `when modify an existent user with valid token with admin permissions and different email between modified and modifier should return OK with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(Roles.ADMIN)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email+"A")

        every{ userServiceMock.update(1, returnedUser, Roles.ADMIN, returnedUser.email+"A")}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).updateUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }

        //every { jwtAccessManagerMock.manage(contextMock) }

    }


    @Test
    fun `when modify an existent user with valid token with admin permissions and equal email between modified and modifier should return OK with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(Roles.ADMIN)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every{ userServiceMock.update(1, returnedUser, Roles.ADMIN, returnedUser.email)}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).updateUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }

        //every { jwtAccessManagerMock.manage(contextMock) }

    }

    @Test
    fun `when modify an existent user with valid token without admin permissions and equal email between modified and modifier should return OK with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(Roles.USER)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every{ userServiceMock.update(1, returnedUser, Roles.USER, returnedUser.email)}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).updateUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }

        //every { jwtAccessManagerMock.manage(contextMock) }

    }


    @Test
    fun `when delete a user by id and it exists, return 200 with user deleted`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).deleteUser(contextMock)

        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }
    }

    @Test
    fun `when delete a user by id and it does not exists, should return not found with status 404 `(){
        val userNotFoundException=UserNotFoundException()
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every{ userServiceMock.delete(1, returnedUser.role, returnedUser.email)}.throws(userNotFoundException)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).deleteUser(contextMock)

        verify { contextMock.json(userNotFoundException.createErrorResponse()).status(HttpStatus.NOT_FOUND_404) }
    }

    @Test
    fun `when delete an existent user with valid token without admin permissions and different email between modified and modifier should return Unauthorized Different User Change exception with 403`(){
        val unauthorizedDifferentUserChangeException=UnauthorizedDifferentUserChangeException()
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every{ userServiceMock.delete(1, returnedUser.role, returnedUser.email)}.throws(unauthorizedDifferentUserChangeException)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).deleteUser(contextMock)

        verify { contextMock.json(unauthorizedDifferentUserChangeException.createErrorResponse()).status(HttpStatus.FORBIDDEN_403) }


    }

    @Test
    fun `when delete an existent user with valid token with admin permissions and different email between modified and modifier should return OK with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(Roles.ADMIN)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email+"A")

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).deleteUser(contextMock)

        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }

    }


    @Test
    fun `when delete an existent user with valid token with admin permissions and equal email between modified and modifier should return OK with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(Roles.ADMIN)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).deleteUser(contextMock)

        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }

    }

    @Test
    fun `when delete an existent user with valid token without admin permissions and equal email between modified and modifier should return OK with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(Roles.USER)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        UserController(userServiceMock, jwtAccessManagerMock).deleteUser(contextMock)

        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }


    }


    @Test
    fun `when a user with valid credentials log in, should return the logged user with status 200`(){
        every { userServiceMock.login(newLoginUser) }.returns(returnedUser)

        every { contextMock.body<UserLogin>() }.returns(newLoginUser)

        UserController(userServiceMock, jwtAccessManagerMock).loginUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }

    }
    @Test
    fun `when a user with non matching credentials log in, should return unauthorized with status 401`(){
        val invalidCredentialsException=InvalidCredentialsException()
        every { userServiceMock.login(newLoginUser) }.throws(invalidCredentialsException)

        every { contextMock.body<UserLogin>() }.returns(newLoginUser)

        UserController(userServiceMock, jwtAccessManagerMock).loginUser(contextMock)

        verify { contextMock.json(invalidCredentialsException.createErrorResponse()).status(HttpStatus.UNAUTHORIZED_401) }

    }
}