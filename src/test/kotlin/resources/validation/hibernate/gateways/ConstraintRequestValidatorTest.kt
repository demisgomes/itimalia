package resources.validation.hibernate.gateways

import com.abrigo.itimalia.factories.UserFactory
import com.abrigo.itimalia.resources.validation.hibernate.entities.UserLoginRequestModel
import com.abrigo.itimalia.resources.validation.hibernate.gateways.ConstraintRequestValidator
import com.abrigo.itimalia.resources.validation.hibernate.utils.MapModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.hibernate.validator.internal.engine.path.PathImpl
import org.junit.Before
import org.junit.Test
import javax.validation.ConstraintViolation
import javax.validation.Validator
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConstraintRequestValidatorTest {
    private val javaxValidatorMock: Validator = mockk(relaxed = true)
    private val constraintRequestValidator = ConstraintRequestValidator(javaxValidatorMock)
    private val emailConstraintViolationMock: ConstraintViolation<UserLoginRequestModel> = mockk(relaxed = true)
    private val blankConstraintViolationMock: ConstraintViolation<UserLoginRequestModel> = mockk(relaxed = true)
    private val emailViolationMessage = "please fill with an email following the pattern: email@email.com"
    private val blankViolationMessage = "this field cannot be blank"

    @Before
    fun setup() {
        mockkObject(MapModel)
    }

    @Test
    fun `when receive a valid model and not have errors, should return an empty map`() {
        val userLoginRequest = UserFactory.sampleLoginRequest()
        val userLoginRequestModel = UserLoginRequestModel("test", "12345")
        every { MapModel.getModel(userLoginRequest) } returns userLoginRequestModel

        assertEquals(constraintRequestValidator.getConstraints(userLoginRequest), emptyMap())
    }

    @Test
    fun `when receive a valid model and have errors, should fulfill the map`() {
        val userLoginRequest = UserFactory.sampleLoginRequest()
        val invalidValue = "test"
        val userLoginRequestModel = UserLoginRequestModel(invalidValue, "12345")
        val message = emailViolationMessage
        every { emailConstraintViolationMock.invalidValue } returns invalidValue
        every { emailConstraintViolationMock.propertyPath } returns PathImpl.createPathFromString("email")
        every { emailConstraintViolationMock.message } returns message

        val javaxConstraints = mutableSetOf(emailConstraintViolationMock)
        every { MapModel.getModel(userLoginRequest) } returns userLoginRequestModel
        every { javaxValidatorMock.validate(userLoginRequestModel) } returns javaxConstraints

        val constraints = constraintRequestValidator.getConstraints(userLoginRequest)

        val violationList = constraints.getValue("email: $invalidValue")

        assertEquals(1, constraints.size)
        assertEquals(1, violationList.size)
        assertEquals("email: $invalidValue", constraints.keys.first())
        assertEquals(message, violationList.first())
    }

    @Test
    fun `when receive a valid model and have two errors by property, should fulfill the map`() {
        val userLoginRequest = UserFactory.sampleLoginRequest()
        val invalidValue = ""
        val userLoginRequestModel = UserLoginRequestModel(invalidValue, "12345")
        val message = emailViolationMessage
        every { emailConstraintViolationMock.invalidValue } returns invalidValue
        every { emailConstraintViolationMock.propertyPath } returns PathImpl.createPathFromString("email")
        every { emailConstraintViolationMock.message } returns message

        every { blankConstraintViolationMock.invalidValue } returns invalidValue
        every { blankConstraintViolationMock.propertyPath } returns PathImpl.createPathFromString("email")
        every { blankConstraintViolationMock.message } returns blankViolationMessage

        val javaxConstraints = mutableSetOf(emailConstraintViolationMock, blankConstraintViolationMock)
        every { MapModel.getModel(userLoginRequest) } returns userLoginRequestModel
        every { javaxValidatorMock.validate(userLoginRequestModel) } returns javaxConstraints

        val constraints = constraintRequestValidator.getConstraints(userLoginRequest)

        val violationList = constraints.getValue("email: $invalidValue")

        assertEquals(1, constraints.size)
        assertEquals("email: $invalidValue", constraints.keys.first())
        assertEquals(2, violationList.size)
        assertTrue(violationList.contains(message))
        assertTrue(violationList.contains(blankViolationMessage))
    }
}
