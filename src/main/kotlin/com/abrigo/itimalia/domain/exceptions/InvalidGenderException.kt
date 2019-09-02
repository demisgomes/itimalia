package com.abrigo.itimalia.domain.exceptions

import org.eclipse.jetty.http.HttpStatus

class InvalidGenderException:ApiException("Invalid gender. Choose male, female, or undefined"){

    override fun httpStatus(): Int {
        return HttpStatus.UNPROCESSABLE_ENTITY_422
    }

    override fun apiError(): ApiError {
        return ApiError.INVALID_GENDER_ERROR
    }

    override fun userResponseMessage(): String {
        return message!!
    }

}