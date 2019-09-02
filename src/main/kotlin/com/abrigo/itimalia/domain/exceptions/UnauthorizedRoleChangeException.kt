package com.abrigo.itimalia.domain.exceptions

import org.eclipse.jetty.http.HttpStatus

class UnauthorizedRoleChangeException :ApiException(message = "You do not have permission for change your role") {
    override fun httpStatus(): Int {
        return HttpStatus.FORBIDDEN_403
    }

    override fun apiError(): ApiError {
        return ApiError.UNAUTHORIZED_ROLE_CHANGE_ERROR
    }

    override fun userResponseMessage(): String {
        return message!!
    }

}
