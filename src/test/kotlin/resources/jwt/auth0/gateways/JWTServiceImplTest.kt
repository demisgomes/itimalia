package resources.jwt.auth0.gateways

import com.abrigo.itimalia.application.config.EnvironmentConfig
import com.abrigo.itimalia.domain.entities.user.UserRole
import com.abrigo.itimalia.domain.exceptions.InvalidTokenException
import com.abrigo.itimalia.factories.TokenFactory
import com.abrigo.itimalia.resources.jwt.auth0.gateways.JWTServiceImpl
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class JWTServiceImplTest {

    private val environmentConfigMock = mockk<EnvironmentConfig>(relaxed = true)
    private val jwtService = JWTServiceImpl(environmentConfigMock)

    @Before
    fun mockEnvironmentConfig() {
        every { environmentConfigMock.jwtSecret() } returns "mysecret"
        every { environmentConfigMock.issuer() } returns "Itimalia-test"
        every { environmentConfigMock.jwtExpirationInMinutes() } returns "5"
    }

    @Test
    fun `given a valid email and role, should sign the token`() {
        val email = "user@itimalia.org"
        val role = UserRole.USER

        val token = jwtService.sign(email, role)

        assertNotNull(token)
    }

    @Test
    fun `given a valid token, should decode the token and get email and role information`() {
        val email = "user@itimalia.org"
        val role = UserRole.USER

        val token = TokenFactory.build(email, role.toString())

        val map = jwtService.decode(token)

        assertNotNull(map)
        assertEquals(email, map[JWTServiceImpl.EMAIL_CLAIM])
        assertEquals(role.toString(), map[JWTServiceImpl.ROLE_CLAIM])
    }

    @Test(expected = InvalidTokenException::class)
    fun `given an invalid token, should expect InvalidTokenException`() {
        val token = TokenFactory.buildInvalid()

        jwtService.decode(token)
    }

    @Test(expected = InvalidTokenException::class)
    fun `given an expired token, should expect InvalidTokenException`() {
        val token = TokenFactory.buildExpired()

        jwtService.decode(token)
    }
}
