package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.domain.entities.user.LoggedUser
import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.User
import com.abrigo.itimalia.domain.entities.user.UserLogin
import com.abrigo.itimalia.domain.entities.user.UserRole
import com.abrigo.itimalia.domain.entities.user.toLoggedUser
import com.abrigo.itimalia.domain.services.AdminService
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
import org.junit.Test
import java.util.Calendar

class AdminControllerTest {
    private lateinit var adminServiceMock: AdminService
    private lateinit var contextMock: Context
    private lateinit var returnedAdminUser: User
    private lateinit var spyReturnedAdminUser: User
    private lateinit var newAdminUser: NewUser
    private lateinit var newLoginUser: UserLogin
    private lateinit var actualDateTime: DateTime

    @Before
    fun setup() {
        adminServiceMock = mockk(relaxed = true)
        contextMock = mockk(relaxed = true)

        mockkStatic(Calendar::class)
        actualDateTime = DateTime.now()

        val formatter = DateTimeFormat.forPattern("dd/mm/yyyy")
        val birthDate = formatter.parseDateTime("01/01/1990")
        returnedAdminUser = UserFactory.sampleDTO(role = UserRole.ADMIN, birthDate = birthDate, creationDate = actualDateTime, modificationDate = actualDateTime)
        newAdminUser = UserFactory.sampleNew()
        newLoginUser = UserFactory.sampleLogin()
        spyReturnedAdminUser = spyk(returnedAdminUser)
    }

    @Test
    fun `when add a valid user with admin permissions should return the user with status 201`() {
        every { adminServiceMock.add(newAdminUser) }.returns(spyReturnedAdminUser)

        every { contextMock.bodyAsClass<NewUser>() }.returns(newAdminUser)

        AdminController(adminServiceMock).addAdminUser(contextMock)

        verify { spyReturnedAdminUser.toLoggedUser() }

        verify { contextMock.json(any<LoggedUser>()) }
        verify { contextMock.status(HttpStatus.CREATED_201) }
    }
}
