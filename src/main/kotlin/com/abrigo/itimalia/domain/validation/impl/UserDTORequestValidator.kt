package com.abrigo.itimalia.domain.validation.impl

import com.abrigo.itimalia.domain.entities.user.UserDTORequest
import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.domain.validation.ConstraintValidator
import com.abrigo.itimalia.domain.validation.Validator

class UserDTORequestValidator(private val constraintValidator: ConstraintValidator<UserDTORequest>):
Validator<UserDTORequest>{
    override fun validate(t: UserDTORequest) {
        val constraints = constraintValidator.getConstraints(t)
        if(constraints.isNotEmpty()) throw ValidationException(constraints)
    }

}