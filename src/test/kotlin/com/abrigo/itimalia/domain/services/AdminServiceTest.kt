package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.User
import com.abrigo.itimalia.domain.entities.user.UserRole
import com.abrigo.itimalia.domain.exceptions.EmailAlreadyExistsException
import com.abrigo.itimalia.domain.exceptions.UserNotFoundException
import com.abrigo.itimalia.domain.jwt.JWTService
import com.abrigo.itimalia.domain.repositories.UserRepository
import com.abrigo.itimalia.factories.UserFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class AdminServiceTest {

    private lateinit var userRepositoryMock: UserRepository
    private lateinit var newUserDTO: User
    private lateinit var birthDate: DateTime
    private lateinit var actualDateTime: DateTime
    private lateinit var jwtService: JWTService
    private lateinit var expectedUser: User
    private lateinit var adminService: AdminService
    private lateinit var newUser: NewUser
    private val passwordServiceMock: PasswordService = mockk(relaxed = true)

    @Before
    fun setup() {
        val formatter = DateTimeFormat.forPattern("dd/mm/yyyy")
        birthDate = formatter.parseDateTime("01/01/1990")
        mockkStatic(DateTime::class)
        actualDateTime = DateTime.now()
        newUserDTO = UserFactory.sampleDTO(id = null, birthDate = birthDate, creationDate = actualDateTime, modificationDate = actualDateTime)
        expectedUser = newUserDTO.copy(id = 1, token = "token_test", password = "encodedPassword")

        newUser = UserFactory.sampleNew(birthDate = birthDate)

        userRepositoryMock = mockk(relaxed = true)

        jwtService = mockk(relaxed = true)

        adminService = AdminServiceImpl(userRepositoryMock, jwtService, passwordServiceMock)
    }

    @Test
    fun `when a valid user with admin permissions request a sign up, register it`() {
        every { DateTime.now() }.returns(actualDateTime)

        every { passwordServiceMock.encode("myPassword") } returns "encodedPassword"

        every { jwtService.sign(newUserDTO.email, UserRole.ADMIN) }.returns("token_test")

        every { userRepositoryMock.add(newUserDTO.copy(role = UserRole.ADMIN, token = "token_test", password = "encodedPassword")) }.returns(expectedUser)

        val userDTO = adminService.add(newUser)

        assertEquals(expectedUser, userDTO)
    }

    @Test(expected = EmailAlreadyExistsException::class)
    fun `when a valid user request a sign up but the email already exists, should expect EmailAlreadyExistsException`() {

        every { DateTime.now() }.returns(actualDateTime)

        every { passwordServiceMock.encode("myPassword") } returns "encodedPassword"

        every { jwtService.sign(newUserDTO.email, UserRole.ADMIN) }.returns("token_test")

        every { userRepositoryMock.add(newUserDTO.copy(role = UserRole.ADMIN, token = "token_test", password = "encodedPassword")) }.throws(EmailAlreadyExistsException())

        adminService.add(newUser)
    }

    @Test(expected = UserNotFoundException::class)
    fun `when a user with invalid id was requested, throws UserNotFoundException`() {
        val userException = UserNotFoundException()
        every { userRepositoryMock.get(844) }.throws(userException)

        adminService.get(844)
    }
}
