package com.abrigo.itimalia.domain.validation.impl

import com.abrigo.itimalia.domain.entities.user.NewUserRequest
import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.domain.validation.ConstraintValidator
import com.abrigo.itimalia.domain.validation.Validator

class NewUserRequestValidator(private val constraintValidator: ConstraintValidator<NewUserRequest>) :
    Validator<NewUserRequest> {
    override fun validate(t: NewUserRequest) {
        val constraints = constraintValidator.getConstraints(t)
        if (constraints.isNotEmpty()) throw ValidationException(constraints)
    }
}
