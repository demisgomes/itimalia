package com.abrigo.itimalia.domain.validation.impl

import com.abrigo.itimalia.domain.entities.animal.AnimalDTORequest
import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.domain.validation.ConstraintValidator
import com.abrigo.itimalia.domain.validation.Validator

class AnimalDTORequestValidator(private val constraintValidator: ConstraintValidator<AnimalDTORequest>):Validator<AnimalDTORequest>{
    override fun validate(t: AnimalDTORequest) {
        val constraints = constraintValidator.getConstraints(t)
        if(constraints.isNotEmpty()) throw ValidationException(constraints)
    }

}