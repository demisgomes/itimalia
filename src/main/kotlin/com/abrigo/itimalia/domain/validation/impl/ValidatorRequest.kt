package com.abrigo.itimalia.domain.validation.impl

import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.domain.validation.ConstraintValidator
import com.abrigo.itimalia.domain.validation.Validator

class ValidatorRequest<T>(private val constraintValidator: ConstraintValidator<T>) : Validator<T> {
    override fun validate(t: T) {
        val constraints = constraintValidator.getConstraints(t)
        if (constraints.isNotEmpty()) throw ValidationException(constraints)
    }
}
