package com.abrigo.itimalia.domain.exceptions

import org.eclipse.jetty.http.HttpStatus

class UserNotFoundException : ApiException("User not found") {
    override fun httpStatus(): Int {
        return HttpStatus.NOT_FOUND_404
    }

    override fun apiError(): ApiError {
        return ApiError.USER_NOT_FOUND_ERROR
    }

    override fun userResponseMessage(): String {
        return message!!
    }

}