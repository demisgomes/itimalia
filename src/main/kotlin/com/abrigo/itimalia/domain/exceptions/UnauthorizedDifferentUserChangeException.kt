package com.abrigo.itimalia.domain.exceptions

import org.eclipse.jetty.http.HttpStatus

class UnauthorizedDifferentUserChangeException :ApiException(message = "You do not have permissions for modifying another user") {
    override fun httpStatus(): Int {
        return HttpStatus.FORBIDDEN_403
    }

    override fun apiError(): ApiError {
        return ApiError.UNAUTHORIZED_DIFFERENT_USER_CHANGE_ERROR
    }

    override fun userResponseMessage(): String {
        return message!!
    }

}
