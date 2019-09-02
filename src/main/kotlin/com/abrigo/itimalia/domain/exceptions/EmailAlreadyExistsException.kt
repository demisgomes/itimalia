package com.abrigo.itimalia.domain.exceptions

import org.eclipse.jetty.http.HttpStatus

class EmailAlreadyExistsException:ApiException(message = "This email already exists") {
    override fun httpStatus(): Int {
        return HttpStatus.UNAUTHORIZED_401
    }

    override fun apiError(): ApiError {
        return ApiError.EMAIL_ALREADY_EXISTS_ERROR
    }

    override fun userResponseMessage(): String {
        return message!!
    }
}