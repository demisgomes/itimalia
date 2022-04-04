package resources.password.bcrypt

import com.abrigo.itimalia.domain.services.PasswordService
import com.abrigo.itimalia.resources.password.bcrypt.BCryptPasswordService
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class BCryptPasswordServiceTest {

    private val passwordService : PasswordService = BCryptPasswordService()

    @Test
    fun `when pass a password, should encode it`(){
        val encodedPassword = passwordService.encode("test")
        assertNotEquals("test", encodedPassword)
    }

    @Test
    fun `when pass an encoded password, should verify the plain-text password successfully`(){
        val encodedPassword = "\$2a\$12\$AxfAtRBqCFa3w4Kl80ijiO.D7aarWR9vKEK9xZKkH4wAOb.RTWi7S"
        assertTrue(passwordService.verify("test", encodedPassword))
    }

    @Test
    fun `when pass an encoded password, should return false when text do not match`(){
        val encodedPassword = "\$2a\$12\$AxfAtRBqCFa3w4Kl80ijiO.D7aarWR9vKEK9xZKkH4wAOb.RTWi7S"
        assertFalse(passwordService.verify("false", encodedPassword))
    }
}