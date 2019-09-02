package com.abrigo.itimalia.domain.exceptions

import org.eclipse.jetty.http.HttpStatus

class AnimalDeadException:ApiException(message = "This animal unfortunately has passed away.") {
    override fun httpStatus(): Int {
        return HttpStatus.FORBIDDEN_403
    }

    override fun apiError(): ApiError {
        return ApiError.ANIMAL_DEAD_ERROR
    }

    override fun userResponseMessage(): String {
        return message!!
    }
}