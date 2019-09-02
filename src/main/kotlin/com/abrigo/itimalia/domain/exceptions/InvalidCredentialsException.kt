package com.abrigo.itimalia.domain.exceptions

import org.eclipse.jetty.http.HttpStatus

class InvalidCredentialsException:ApiException(message = "Invalid user and/or password") {
    override fun httpStatus(): Int {
        return HttpStatus.UNAUTHORIZED_401
    }

    override fun apiError(): ApiError {
        return ApiError.INVALID_CREDENTIALS_ERROR
    }

    override fun userResponseMessage(): String {
        return message!!
    }
}