package com.abrigo.itimalia.resources.validation.hibernate.gateways

import com.abrigo.itimalia.domain.validation.ConstraintValidator
import com.abrigo.itimalia.resources.validation.hibernate.utils.MapModel.getModel
import javax.validation.ConstraintViolation
import javax.validation.Validator

class ConstraintModelValidator<T>(private val javaxValidator: Validator) :
    ConstraintValidator<T> {

    override fun getConstraints(t: T): HashMap<String, MutableList<String>> {
        val model = getModel(t)
        val constraints = javaxValidator.validate(model)
        return mount(constraints)
    }

    private fun mount(constraints: Set<ConstraintViolation<*>>): HashMap<String, MutableList<String>> {
        val errors = hashMapOf<String, MutableList<String>>()
        if (constraints.isNotEmpty()) {
            constraints.forEach { violation ->
                val key = "${violation.propertyPath}: ${violation.invalidValue}"
                if (errors.containsKey(key)) {
                    errors[key]?.add(violation.message)
                } else {
                    errors[key] = mutableListOf(violation.message)
                }
            }
        }
        return errors
    }
}
