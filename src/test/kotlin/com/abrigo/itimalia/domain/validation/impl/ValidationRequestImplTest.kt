package com.abrigo.itimalia.domain.validation.impl

import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.domain.validation.ConstraintValidator
import com.abrigo.itimalia.domain.validation.Request
import com.abrigo.itimalia.factories.UserFactory
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class ValidationRequestImplTest {
    val constraintValidatorMock: ConstraintValidator<Request> = mockk(relaxed = true)
    val validationRequest = ValidatorRequestImpl(constraintValidatorMock)

    @Test
    fun `when a class has no errors should validate`() {
        every { constraintValidatorMock.getConstraints(any()) } returns hashMapOf()

        validationRequest.validate(UserFactory.sampleNewRequest())
    }

    @Test(expected = ValidationException::class)
    fun `when a class has errors should expect ValidationException`() {
        every { constraintValidatorMock.getConstraints(any()) } returns hashMapOf("email: test" to mutableListOf("please put an email"))

        validationRequest.validate(UserFactory.sampleNewRequest())
    }
}
