package com.abrigo.itimalia.domain.exceptions

import org.eclipse.jetty.http.HttpStatus

class AnimalAlreadyAdoptedException :ApiException(message = "This animal already was adopted") {
    override fun httpStatus(): Int {
        return HttpStatus.UNAUTHORIZED_401
    }

    override fun apiError(): ApiError {
        return ApiError.ANIMAL_ALREADY_ADOPTED_ERROR
    }

    override fun userResponseMessage(): String {
        return message!!
    }
}