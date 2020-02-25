package com.abrigo.itimalia.domain.validation.impl

import com.abrigo.itimalia.domain.entities.animal.NewAnimalRequest
import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.domain.validation.ConstraintValidator
import com.abrigo.itimalia.domain.validation.Validator

class NewAnimalRequestValidator(private val constraintValidator: ConstraintValidator<NewAnimalRequest>):
    Validator<NewAnimalRequest> {
    override fun validate(t: NewAnimalRequest) {
        val constraints = constraintValidator.getConstraints(t)
        if(constraints.isNotEmpty()) throw ValidationException(constraints)
    }

}