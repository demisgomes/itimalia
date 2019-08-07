package application.web.controllers

import domain.entities.*
import domain.jwt.JWTAccessManager
import domain.repositories.factories.UserFactory
import domain.services.AdminService
import io.javalin.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.eclipse.jetty.http.HttpStatus
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.Before
import org.junit.Test
import java.util.*

class AdminControllerTest{
    lateinit var adminServiceMock: AdminService
    lateinit var contextMock: Context
    lateinit var returnedAdminUser: UserDTO
    lateinit var newAdminUser: NewUser
    lateinit var newLoginUser: UserLogin
    lateinit var jwtAccessManagerMock: JWTAccessManager
    lateinit var actualDateTime:DateTime

    @Before
    fun setup(){
        adminServiceMock = mockk(relaxed=true)
        contextMock = mockk(relaxed = true)
        jwtAccessManagerMock= mockk(relaxed = true)

        mockkStatic(Calendar::class)
        actualDateTime= DateTime.now()


        val formatter = DateTimeFormat.forPattern("dd/mm/yyyy")
        val birthDate=formatter.parseDateTime("01/01/1990")
        returnedAdminUser= UserFactory.sampleDTO(role = Roles.ADMIN, birthDate = birthDate, creationDate = actualDateTime, modificationDate = actualDateTime)
        newAdminUser = UserFactory.sampleNew(role = Roles.ADMIN)
        newLoginUser = UserFactory.sampleLogin()
    }

    @Test
    fun `when add a valid user with admin permissions should return the user with status 201`(){

        every{ adminServiceMock.add(newAdminUser, Roles.ADMIN)}.returns(returnedAdminUser)

        every { contextMock.body<NewUser>() }.returns(newAdminUser)

        every { jwtAccessManagerMock.extractRole(contextMock) }.returns(Roles.ADMIN)

        AdminController(adminServiceMock, jwtAccessManagerMock).addAdminUser(contextMock)

        verify { contextMock.json(returnedAdminUser).status(HttpStatus.CREATED_201) }
    }

}