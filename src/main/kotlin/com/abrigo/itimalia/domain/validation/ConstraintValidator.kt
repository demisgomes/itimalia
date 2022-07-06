package com.abrigo.itimalia.domain.validation

interface ConstraintValidator<T> {
    fun getConstraints(t: T): HashMap<String, MutableList<String>>
}
