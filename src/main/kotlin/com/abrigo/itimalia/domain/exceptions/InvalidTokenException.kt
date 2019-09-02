package com.abrigo.itimalia.domain.exceptions

import org.eclipse.jetty.http.HttpStatus

class InvalidTokenException:ApiException(message = "This token has expired or not exists") {
    override fun httpStatus(): Int {
        return HttpStatus.UNAUTHORIZED_401
    }

    override fun apiError(): ApiError {
        return ApiError.INVALID_TOKEN_ERROR
    }

    override fun userResponseMessage(): String {
        return message!!
    }
}