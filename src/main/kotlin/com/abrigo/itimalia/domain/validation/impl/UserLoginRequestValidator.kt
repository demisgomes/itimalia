package com.abrigo.itimalia.domain.validation.impl

import com.abrigo.itimalia.domain.entities.user.UserLoginRequest
import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.domain.validation.ConstraintValidator
import com.abrigo.itimalia.domain.validation.Validator

data class UserLoginRequestValidator(private val constraintValidator: ConstraintValidator<UserLoginRequest>) : Validator<UserLoginRequest> {
    override fun validate(t: UserLoginRequest) {
        val constraints = constraintValidator.getConstraints(t)
        if (constraints.isNotEmpty()) throw ValidationException(constraints)
    }
}
