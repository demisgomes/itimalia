package com.abrigo.itimalia.application.web.extensions

import com.abrigo.itimalia.domain.exceptions.ValidationException
import io.javalin.http.Context

inline fun <reified E : Enum<E>> Context.queryParamAsEnum(key: String): E? {
    val value = this.queryParam(key)
    if (value.isNullOrBlank()) return null
    return try {
        enumValueOf<E>(value.uppercase())
    } catch (exception: IllegalArgumentException) {
        // log this error
        throw ValidationException(mapOf(key to mutableListOf("Invalid field '$value'. Please update this value with expected values: ${enumValues<E>().map { it.name }}")))
    }
}
