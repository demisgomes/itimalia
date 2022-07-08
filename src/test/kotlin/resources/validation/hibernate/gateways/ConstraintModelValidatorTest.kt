package resources.validation.hibernate.gateways

import com.abrigo.itimalia.domain.entities.user.UserLoginRequest
import com.abrigo.itimalia.factories.UserFactory
import com.abrigo.itimalia.resources.validation.hibernate.entities.UserLoginRequestModel
import com.abrigo.itimalia.resources.validation.hibernate.gateways.ConstraintModelValidator
import com.abrigo.itimalia.resources.validation.hibernate.utils.MapModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.Before
import org.junit.Test
import javax.validation.Validator
import kotlin.test.assertEquals

class ConstraintModelValidatorTest {
    val javaxValidatorMock: Validator = mockk(relaxed = true)
    val constraintModelValidator = ConstraintModelValidator<UserLoginRequest>(javaxValidatorMock)

    @Before
    fun setup() {
        mockkObject(MapModel)
    }

    @Test
    fun `when receive a valid model and not have errors, should return an empty map`() {
        val userLoginRequest = UserFactory.sampleLoginRequest()
        val userLoginRequestModel = UserLoginRequestModel("test", "12345")
        every { MapModel.getModel(userLoginRequest) } returns userLoginRequestModel

        assertEquals(constraintModelValidator.getConstraints(userLoginRequest), emptyMap())
    }
}
