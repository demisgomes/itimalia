package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.application.web.accessmanagers.JWTAccessManager
import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.User
import com.abrigo.itimalia.domain.entities.user.UserLogin
import com.abrigo.itimalia.domain.entities.user.UserRole
import com.abrigo.itimalia.domain.services.AdminService
import com.abrigo.itimalia.factories.UserFactory
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
import java.util.*

class AdminControllerTest{
    lateinit var adminServiceMock: AdminService
    lateinit var contextMock: Context
    lateinit var returnedAdminUser: User
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
        returnedAdminUser= UserFactory.sampleDTO(role = UserRole.ADMIN, birthDate = birthDate, creationDate = actualDateTime, modificationDate = actualDateTime)
        newAdminUser = UserFactory.sampleNew()
        newLoginUser = UserFactory.sampleLogin()
    }

    @Test
    fun `when add a valid user with admin permissions should return the user with status 201`(){

        every{ adminServiceMock.add(newAdminUser, UserRole.ADMIN)}.returns(returnedAdminUser)

        every { contextMock.body<NewUser>() }.returns(newAdminUser)

        every { jwtAccessManagerMock.extractRole(contextMock) }.returns(UserRole.ADMIN)

        AdminController(adminServiceMock, jwtAccessManagerMock).addAdminUser(contextMock)

        verify { contextMock.json(returnedAdminUser).status(HttpStatus.CREATED_201) }
    }

}