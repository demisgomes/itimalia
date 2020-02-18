package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.domain.entities.Roles
import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.NewUserRequest
import com.abrigo.itimalia.domain.entities.user.UserDTO
import com.abrigo.itimalia.domain.entities.user.UserDTORequest
import com.abrigo.itimalia.domain.entities.user.UserLogin
import com.abrigo.itimalia.domain.entities.user.UserLoginRequest
import com.abrigo.itimalia.domain.entities.user.UserSearched
import com.abrigo.itimalia.domain.jwt.JWTAccessManager
import com.abrigo.itimalia.domain.repositories.factories.UserFactory
import com.abrigo.itimalia.domain.services.UserService
import com.abrigo.itimalia.domain.validation.Validator
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
    private lateinit var userServiceMock: UserService
    private lateinit var contextMock: Context
    private lateinit var returnedUser: UserDTO
    private lateinit var newUser: NewUser
    private lateinit var newLoginUser: UserLogin
    private lateinit var newLoginRequest: UserLoginRequest
    private lateinit var newUserRequest: NewUserRequest
    private lateinit var userDTORequest: UserDTORequest
    private lateinit var jwtAccessManagerMock: JWTAccessManager
    private lateinit var actualDateTime:DateTime
    private lateinit var validatorNewUser: Validator<NewUserRequest>
    private lateinit var validatorUserDTO: Validator<UserDTORequest>
    private lateinit var validatorUserLogin: Validator<UserLoginRequest>
    private lateinit var userController: UserController

    @Before
    fun setup(){
        userServiceMock = mockk(relaxed=true)
        validatorNewUser = mockk(relaxed = true)
        validatorUserDTO = mockk(relaxed = true)
        validatorUserLogin = mockk(relaxed = true)
        contextMock = mockk(relaxed = true)
        jwtAccessManagerMock=mockk(relaxed = true)
        actualDateTime= DateTime.now()
        mockkStatic(UserSearched::class)
        val formatter = DateTimeFormat.forPattern("dd/mm/yyyy")
        val birthDate=formatter.parseDateTime("01/01/1990")
        returnedUser= UserFactory.sampleDTO(birthDate = birthDate, creationDate = actualDateTime, modificationDate = actualDateTime)
        newUser = UserFactory.sampleNew()
        newUserRequest = UserFactory.sampleNewRequest()
        userDTORequest = UserFactory.sampleDTORequest(birthDate = birthDate, creationDate = actualDateTime, modificationDate = actualDateTime)
        newLoginUser = UserFactory.sampleLogin()
        newLoginRequest = UserFactory.sampleLoginRequest()
        userController = UserController(userServiceMock, jwtAccessManagerMock, validatorNewUser, validatorUserDTO, validatorUserLogin)
    }

    @Test
    fun `when request a user with a valid id, return it`(){
        every{ userServiceMock.get(1)}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        userController.findUser(contextMock)

        verify { contextMock.json(any<UserSearched>()) }
        verify { contextMock.status(HttpStatus.OK_200) }
    }

    @Test
    fun `when add a valid user should return the user with status 201`(){
        every{ userServiceMock.add(newUser)}.returns(returnedUser)

        every { contextMock.body<NewUserRequest>() }.returns(newUserRequest)

        userController.addUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.CREATED_201) }
    }

    @Test
    fun `when modify a valid user should return the user modified with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every{ userServiceMock.update(1, returnedUser, returnedUser.role, returnedUser.email)}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTORequest>() }.returns(userDTORequest)

        userController.updateUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when modify an existent user with valid token with admin permissions and different email between modified and modifier should return OK with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(Roles.ADMIN)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email+"A")

        every{ userServiceMock.update(1, returnedUser, Roles.ADMIN, returnedUser.email+"A")}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTORequest>() }.returns(userDTORequest)

        userController.updateUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }

    }


    @Test
    fun `when modify an existent user with valid token with admin permissions and equal email between modified and modifier should return OK with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(Roles.ADMIN)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every{ userServiceMock.update(1, returnedUser, Roles.ADMIN, returnedUser.email)}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTORequest>() }.returns(userDTORequest)

        userController.updateUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }

    }

    @Test
    fun `when modify an existent user with valid token without admin permissions and equal email between modified and modifier should return OK with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(Roles.USER)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every{ userServiceMock.update(1, returnedUser, Roles.USER, returnedUser.email)}.returns(returnedUser)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTORequest>() }.returns(userDTORequest)

        userController.updateUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }

    }

    @Test
    fun `when delete a user by id and it exists, return 200 with user deleted`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(returnedUser.role)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        userController.deleteUser(contextMock)

        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }
    }

    @Test
    fun `when delete an existent user with valid token with admin permissions and different email between modified and modifier should return OK with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(Roles.ADMIN)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email+"A")

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        userController.deleteUser(contextMock)

        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }

    }

    @Test
    fun `when delete an existent user with valid token with admin permissions and equal email between modified and modifier should return OK with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(Roles.ADMIN)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        userController.deleteUser(contextMock)

        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }

    }

    @Test
    fun `when delete an existent user with valid token without admin permissions and equal email between modified and modifier should return OK with status 200`(){
        every { jwtAccessManagerMock.extractRole(contextMock)}.returns(Roles.USER)

        every { jwtAccessManagerMock.extractEmail(contextMock)}.returns(returnedUser.email)

        every { contextMock.pathParam("id") }.returns("1")

        every { contextMock.body<UserDTO>() }.returns(returnedUser)

        userController.deleteUser(contextMock)

        verify { contextMock.status(HttpStatus.NO_CONTENT_204) }


    }

    @Test
    fun `when a user with valid credentials log in, should return the logged user with status 200`(){
        every { userServiceMock.login(newLoginUser) }.returns(returnedUser)

        every { contextMock.body<UserLoginRequest>() }.returns(newLoginRequest)

        userController.loginUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }

    }

}