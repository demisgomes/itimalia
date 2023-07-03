package resources.password.bcrypt

import com.abrigo.itimalia.application.config.EnvironmentConfig
import com.abrigo.itimalia.domain.services.PasswordService
import com.abrigo.itimalia.resources.password.bcrypt.BCryptPasswordService
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class BCryptPasswordServiceTest {

    private val environmentConfigMock = mockk<EnvironmentConfig>(relaxed = true)
    private val passwordService: PasswordService = BCryptPasswordService(environmentConfigMock)

    @Test
    fun `when pass a password, should encode it`() {
        every { environmentConfigMock.hashSalt() } returns "12"
        val encodedPassword = passwordService.encode("test")
        assertNotEquals("test", encodedPassword)
    }

    @Test
    fun `when pass an encoded password, should verify the plain-text password successfully`() {
        val encodedPassword = "\$2a\$12\$AxfAtRBqCFa3w4Kl80ijiO.D7aarWR9vKEK9xZKkH4wAOb.RTWi7S"
        assertTrue(passwordService.verify("test", encodedPassword))
    }

    @Test
    fun `when pass an encoded password, should return false when text do not match`() {
        val encodedPassword = "\$2a\$12\$AxfAtRBqCFa3w4Kl80ijiO.D7aarWR9vKEK9xZKkH4wAOb.RTWi7S"
        assertFalse(passwordService.verify("false", encodedPassword))
    }
}
