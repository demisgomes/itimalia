package com.abrigo.itimalia.resources.validation.hibernate.utils

import javax.validation.ConstraintViolation

class MapMounter {
    fun mount(constraints: Set<ConstraintViolation<*>>): HashMap<String, MutableList<String>> {
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
