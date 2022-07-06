package com.abrigo.itimalia.domain.exceptions

import org.eclipse.jetty.http.HttpStatus

class AnimalGoneException : ApiException(message = "This animal has gone from the shelter.") {
    override fun httpStatus(): Int {
        return HttpStatus.FORBIDDEN_403
    }

    override fun apiError(): ApiError {
        return ApiError.ANIMAL_GONE_ERROR
    }

    override fun userResponseMessage(): String {
        return message!!
    }
}
