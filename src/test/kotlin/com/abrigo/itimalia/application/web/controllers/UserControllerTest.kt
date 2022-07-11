package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.application.web.accessmanagers.JWTAccessManager
import com.abrigo.itimalia.domain.entities.user.LoggedUser
import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.NewUserRequest
import com.abrigo.itimalia.domain.entities.user.User
import com.abrigo.itimalia.domain.entities.user.UserLogin
import com.abrigo.itimalia.domain.entities.user.UserLoginRequest
import com.abrigo.itimalia.domain.entities.user.UserPublicInfo
import com.abrigo.itimalia.domain.entities.user.UserRequest
import com.abrigo.itimalia.domain.entities.user.UserRole
import com.abrigo.itimalia.domain.entities.user.toLoggedUser
import com.abrigo.itimalia.domain.services.UserService
import com.abrigo.itimalia.factories.UserFactory
import io.javalin.http.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import org.eclipse.jetty.http.HttpStatus
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

// a particular behavior is breaking the tests: https://github.com/mockk/mockk/issues/502
@Ignore
class UserControllerTest {
    private lateinit var userServiceMock: UserService
    private lateinit var returnedUser: User
    private lateinit var newUser: NewUser
    private lateinit var newLoginUser: UserLogin
    private lateinit var newLoginRequest: UserLoginRequest
    private lateinit var newUserRequest: NewUserRequest
    private lateinit var userRequest: UserRequest
    private lateinit var jwtAccessManagerMock: JWTAccessManager
    private lateinit var actualDateTime: DateTime
    private lateinit var userController: UserController
    private lateinit var spyReturnedUser: User

    private val contextMock: Context = mockk(relaxed = true)

    @Before
    fun setup() {
        userServiceMock = mockk(relaxed = true)
        jwtAccessManagerMock = mockk(relaxed = true)
        actualDateTime = DateTime.now()
        mockkStatic(UserPublicInfo::class)
        val formatter = DateTimeFormat.forPattern("dd/mm/yyyy")
        val birthDate = formatter.parseDateTime("01/01/1990")
        returnedUser = UserFactory.sampleDTO(birthDate = birthDate, creationDate = actualDateTime, modificationDate = actualDateTime)
        newUser = UserFactory.sampleNew()
        newUserRequest = UserFactory.sampleNewRequest()
        userRequest = UserFactory.sampleDTORequest(birthDate = birthDate, creationDate = actualDateTime, modificationDate = actualDateTime)
        newLoginUser = UserFactory.sampleLogin()
        newLoginRequest = UserFactory.sampleLoginRequest()
        userController = UserController(userServiceMock, jwtAccessManagerMock)
        spyReturnedUser = spyk(returnedUser)
    }

    @Test
    fun `when request a user with a valid id, return it`() {
        every { userServiceMock.get(1) }.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        userController.findUser(contextMock)

        verify { contextMock.json(any<UserPublicInfo>()) }
        verify { contextMock.status(HttpStatus.OK_200) }
    }

    @Test
    fun `when add a valid user should return the user with status 201`() {
        every { userServiceMock.add(newUserRequest) }.returns(spyReturnedUser)

        every { contextMock.bodyAsClass<NewUserRequest>() }.returns(newUserRequest)

        userController.addUser(contextMock)

        verify { spyReturnedUser.toLoggedUser() }

        verify { contextMock.json(any<LoggedUser>()) }
        verify { contextMock.status(HttpStatus.CREATED_201) }
    }

    @Test
    fun `when modify a valid user should return the user modified with status 200`() {
        every { jwtAccessManagerMock.extractRole(contextMock) }.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock) }.returns(returnedUser.email)

        every { userServiceMock.update(1, userRequest, returnedUser.role, returnedUser.email) }.returns(spyReturnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.bodyAsClass<UserRequest>() }.returns(userRequest)

        userController.updateUser(contextMock)

        verify { spyReturnedUser.toLoggedUser() }

        verify { contextMock.json(any<LoggedUser>()) }
        verify { contextMock.status(HttpStatus.OK_200) }
    }

    @Test
    fun `when modify an existent user with valid token with admin permissions and different email between modified and modifier should return OK with status 200`() {
        every { jwtAccessManagerMock.extractRole(contextMock) }.returns(UserRole.ADMIN)

        every { jwtAccessManagerMock.extractEmail(contextMock) }.returns(returnedUser.email + "A")

        every { userServiceMock.update(1, userRequest, UserRole.ADMIN, returnedUser.email + "A") }.returns(spyReturnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.bodyAsClass<UserRequest>() }.returns(userRequest)

        userController.updateUser(contextMock)

        verify { spyReturnedUser.toLoggedUser() }

        verify { contextMock.json(any<LoggedUser>()) }
        verify { contextMock.status(HttpStatus.OK_200) }
    }

    @Test
    fun `when modify an existent user with valid token with admin permissions and equal email between modified and modifier should return OK with status 200`() {
        every { jwtAccessManagerMock.extractRole(contextMock) }.returns(UserRole.ADMIN)

        every { jwtAccessManagerMock.extractEmail(contextMock) }.returns(returnedUser.email)

        every { userServiceMock.update(1, userRequest, UserRole.ADMIN, returnedUser.email) }.returns(spyReturnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.bodyAsClass<UserRequest>() }.returns(userRequest)

        userController.updateUser(contextMock)

        verify { spyReturnedUser.toLoggedUser() }

        verify { contextMock.json(any<LoggedUser>()) }
        verify { contextMock.status(HttpStatus.OK_200) }
    }

    @Test
    fun `when modify an existent user with valid token without admin permissions and equal email between modified and modifier should return OK with status 200`() {
        every { jwtAccessManagerMock.extractRole(contextMock) }.returns(UserRole.USER)

        every { jwtAccessManagerMock.extractEmail(contextMock) }.returns(returnedUser.email)

        every { userServiceMock.update(1, userRequest, UserRole.USER, returnedUser.email) }.returns(spyReturnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.bodyAsClass<UserRequest>() }.returns(userRequest)

        userController.updateUser(contextMock)

        verify { spyReturnedUser.toLoggedUser() }

        verify { contextMock.json(any<LoggedUser>()) }
        verify { contextMock.status(HttpStatus.OK_200) }
    }

    @Test
    fun `when delete a user by id and it exists, return 200 with user deleted`() {
        every { jwtAccessManagerMock.extractRole(contextMock) }.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock) }.returns(returnedUser.email)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.bodyAsClass<User>() }.returns(returnedUser)

        userController.deleteUser(contextMock)

        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }
    }

    @Test
    fun `when delete an existent user with valid token with admin permissions and different email between modified and modifier should return OK with status 200`() {
        every { jwtAccessManagerMock.extractRole(contextMock) }.returns(UserRole.ADMIN)

        every { jwtAccessManagerMock.extractEmail(contextMock) }.returns(returnedUser.email + "A")

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.bodyAsClass<User>() }.returns(returnedUser)

        userController.deleteUser(contextMock)

        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }
    }

    @Test
    fun `when delete an existent user with valid token with admin permissions and equal email between modified and modifier should return OK with status 200`() {
        every { jwtAccessManagerMock.extractRole(contextMock) }.returns(UserRole.ADMIN)

        every { jwtAccessManagerMock.extractEmail(contextMock) }.returns(returnedUser.email)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.bodyAsClass<User>() }.returns(returnedUser)

        userController.deleteUser(contextMock)

        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }
    }

    @Test
    fun `when delete an existent user with valid token without admin permissions and equal email between modified and modifier should return OK with status 200`() {
        every { jwtAccessManagerMock.extractRole(contextMock) }.returns(UserRole.USER)

        every { jwtAccessManagerMock.extractEmail(contextMock) }.returns(returnedUser.email)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.bodyAsClass<User>() }.returns(returnedUser)

        userController.deleteUser(contextMock)

        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }
    }

    @Test
    fun `when a user with valid credentials log in, should return the logged user with status 200`() {
        every { userServiceMock.login(newLoginRequest) }.returns(spyReturnedUser)

        every { contextMock.bodyAsClass<UserLoginRequest>() }.returns(newLoginRequest)

        userController.loginUser(contextMock)

        verify { spyReturnedUser.toLoggedUser() }

        verify { contextMock.json(any<LoggedUser>()) }
        verify { contextMock.status(HttpStatus.OK_200) }
    }
}
