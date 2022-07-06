package com.abrigo.itimalia.domain.validation.impl

import com.abrigo.itimalia.domain.entities.animal.AnimalRequest
import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.domain.validation.ConstraintValidator
import com.abrigo.itimalia.domain.validation.Validator

class AnimalRequestValidator(private val constraintValidator: ConstraintValidator<AnimalRequest>) : Validator<AnimalRequest> {
    override fun validate(t: AnimalRequest) {
        val constraints = constraintValidator.getConstraints(t)
        if (constraints.isNotEmpty()) throw ValidationException(constraints)
    }
}
