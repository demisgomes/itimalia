package application.web.controllers

import domain.entities.Gender
import domain.entities.UserDTO
import domain.exceptions.UserNotFoundException
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

class ItimaliaControllerTest{
    lateinit var userServiceMock: UserService
    lateinit var contextMock: Context
    lateinit var returnedUser:UserDTO

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
            false,
            null,
            null)
    }

    @Test
    fun `when request a user with a valid id, return it`(){
        every{ userServiceMock.get(1)}.returns(returnedUser)

        ItimaliaController(userServiceMock).findUser(contextMock)

        verify { contextMock.json(returnedUser).status(HttpStatus.OK_200) }
    }

    @Test
    fun `when request a user with an invalid id, return not found`(){
        val userNotFoundException=UserNotFoundException("User Not Found")
        every{ userServiceMock.get(1)}.throws(userNotFoundException)

        ItimaliaController(userServiceMock).findUser(contextMock)

        verify { contextMock.json(userNotFoundException.userResponseMessage()).status(HttpStatus.NOT_FOUND_404) }
    }
}