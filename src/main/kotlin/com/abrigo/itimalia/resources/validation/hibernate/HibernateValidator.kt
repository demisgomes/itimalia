package com.abrigo.itimalia.resources.validation.hibernate

import javax.validation.Validation
import javax.validation.Validator

object HibernateValidator {
    fun create(): Validator {
        val factory = Validation.buildDefaultValidatorFactory()
        return factory.validator
    }
}
