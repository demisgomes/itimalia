package com.abrigo.itimalia.domain.validation

interface ValidatorRequest<T : Request> {
    fun validate(t: T)
}
