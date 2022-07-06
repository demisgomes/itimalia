package com.abrigo.itimalia.domain.validation.impl

import com.abrigo.itimalia.domain.entities.user.UserRequest
import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.domain.validation.ConstraintValidator
import com.abrigo.itimalia.domain.validation.Validator

class UserDTORequestValidator(private val constraintValidator: ConstraintValidator<UserRequest>) :
    Validator<UserRequest> {
    override fun validate(t: UserRequest) {
        val constraints = constraintValidator.getConstraints(t)
        if (constraints.isNotEmpty()) throw ValidationException(constraints)
    }
}
