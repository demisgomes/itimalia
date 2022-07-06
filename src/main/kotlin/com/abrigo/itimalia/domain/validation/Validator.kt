package com.abrigo.itimalia.domain.validation

interface Validator<T> {
    fun validate(t: T)
}
