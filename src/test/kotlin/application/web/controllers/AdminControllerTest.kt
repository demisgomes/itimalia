package application.web.controllers

import domain.entities.*
import domain.jwt.JWTAccessManager
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

class AdminControllerTest{
    lateinit var userServiceMock: UserService
    lateinit var contextMock: Context
    lateinit var returnedAdminUser: UserDTO
    lateinit var newAdminUser: NewUser
    lateinit var newLoginUser: UserLogin
    lateinit var jwtAccessManagerMock: JWTAccessManager

    @Before
    fun setup(){
        userServiceMock = mockk(relaxed=true)
        contextMock = mockk(relaxed = true)
        jwtAccessManagerMock= mockk(relaxed = true)

        val formatter: DateFormat = SimpleDateFormat("dd/mm/yyyy")
        val date=formatter.parse("01/01/1990")
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

        newAdminUser = NewUser(
            "newUser@domain.com",
            "password",
            date,
            Gender.MASC,
            "New User",
            "81823183183"
        )

        newLoginUser = UserLogin(
            "newUser@domain.com",
            "password"
        )
    }

    @Test
    fun `when add a valid user with admin permissions should return the user with status 201`(){

        every{ userServiceMock.add(newAdminUser)}.returns(returnedAdminUser)

        every { contextMock.body<NewUser>() }.returns(newAdminUser)

        UserController(userServiceMock, jwtAccessManagerMock).addUser(contextMock)

        verify { contextMock.json(returnedAdminUser).status(HttpStatus.CREATED_201) }
    }

}