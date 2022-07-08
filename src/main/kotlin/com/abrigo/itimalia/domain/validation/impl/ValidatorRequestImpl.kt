package com.abrigo.itimalia.domain.validation.impl

import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.domain.validation.ConstraintValidator
import com.abrigo.itimalia.domain.validation.Request
import com.abrigo.itimalia.domain.validation.ValidatorRequest

class ValidatorRequestImpl(private val constraintValidator: ConstraintValidator<Request>) : ValidatorRequest<Request> {
    override fun validate(t: Request) {
        val constraints = constraintValidator.getConstraints(t)
        if (constraints.isNotEmpty()) throw ValidationException(constraints)
    }
}
