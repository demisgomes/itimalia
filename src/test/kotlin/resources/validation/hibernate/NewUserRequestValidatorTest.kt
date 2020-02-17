package resources.validation.hibernate

import com.abrigo.itimalia.domain.entities.Gender
import com.abrigo.itimalia.resources.validation.hibernate.entities.NewUserRequestModel
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.junit.Test
import javax.validation.Validation
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NewUserRequestValidatorTest{
    private val validator = Validation.buildDefaultValidatorFactory().validator
    private val underMinimumDateTime = LocalDate.now().minusYears(12).toDateTime(LocalTime())
    private val validDateTime = LocalDate.now().minusYears(16).toDateTime(LocalTime())

    private val validNewUserRequest = NewUserRequestModel("email@email.com", "password", validDateTime, Gender.FEMALE, "name", "81")

    @Test
    fun `given an user with null email, should return one constraint with message 'please fill with an email'`(){
        val newUserRequest = validNewUserRequest.copy(email = null)
        val constraints = validator.validate(newUserRequest)
        val constraint = constraints.iterator().next()
        assertEquals("please fill with an email", constraint.message)
        assertEquals(1,constraints.size)
    }

    @Test
    fun `given an user with invalid non-null email, should return one constraint with message 'please fill with an email following the pattern email@emailcom'`(){
        val newUserRequest = validNewUserRequest.copy(email = "email")
        val constraints = validator.validate(newUserRequest)
        val constraint = constraints.iterator().next()
        assertEquals("please fill with an email following the pattern: email@email.com", constraint.message)
        assertEquals(1,constraints.size)
    }

    @Test
    fun `given an user with null name, should return one constraint with message 'please fill with a name'`(){
        val newUserRequest = validNewUserRequest.copy(name = null)
        val constraints = validator.validate(newUserRequest)
        val constraint = constraints.iterator().next()
        assertEquals("please fill with a name", constraint.message)
        assertEquals(1,constraints.size)
    }

    @Test
    fun `given an user with less than 13 years old, should return one constraint with message 'please fill a birthDate with pattern yyyy-MM-dd of a user with more than 13 years old'`(){
        val newUserRequest = validNewUserRequest.copy(birthDate = underMinimumDateTime)
        val constraints = validator.validate(newUserRequest)
        val constraint = constraints.iterator().next()
        assertEquals("please fill a birthDate with pattern yyyy-MM-dd of a user with more than 13 years old", constraint.message)
        assertEquals(1,constraints.size)
    }

    @Test
    fun `given an user with null date, should return one constraint with message 'please fill a birthDate with pattern yyyy-MM-dd of a user with more than 13 years old'`(){
        val newUserRequest = validNewUserRequest.copy(birthDate = null)
        val constraints = validator.validate(newUserRequest)
        val constraint = constraints.iterator().next()
        assertEquals("please fill a birthDate with pattern yyyy-MM-dd of a user with more than 13 years old", constraint.message)
        assertEquals(1,constraints.size)
    }

    @Test
    fun `given an user with null phone, should return one constraint with message 'please fill with a phone'`(){
        val newUserRequest = validNewUserRequest.copy(phone = null)
        val constraints = validator.validate(newUserRequest)
        val constraint = constraints.iterator().next()
        assertEquals("please fill with a phone", constraint.message)
        assertEquals(1,constraints.size)
    }

    @Test
    fun `given an user with null password, should return one constraint with message 'please fill with a password'`(){
        val newUserRequest = validNewUserRequest.copy(password = null)
        val constraints = validator.validate(newUserRequest)
        val constraint = constraints.iterator().next()
        assertEquals("please fill with a password", constraint.message)
        assertEquals(1,constraints.size)
    }

    @Test
    fun `given an user with null gender, should return one constraint with message 'please fill with a gender'`(){
        val newUserRequest = validNewUserRequest.copy(gender = null)
        val constraints = validator.validate(newUserRequest)
        val constraint = constraints.iterator().next()
        assertEquals("please fill with a gender: female, male, or not_declared", constraint.message)
        assertEquals(1,constraints.size)
    }

    @Test
    fun `given an user with null phone and password, should return two constraints with messages 'please fill with a phone' and 'please fill with a password'`(){
        val newUserRequest = validNewUserRequest.copy(phone = null, password = null)
        val constraints = validator.validate(newUserRequest)
        val constraintMessages = ArrayList<String>()
        constraints.forEach { constraint ->
            constraintMessages.add(constraint.message)
        }
        assertTrue(constraintMessages.contains("please fill with a phone"))
        assertTrue(constraintMessages.contains("please fill with a password"))
        assertEquals(2,constraints.size)
    }
}