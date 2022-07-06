package com.abrigo.itimalia.domain.exceptions

import org.eclipse.jetty.http.HttpStatus

class UnauthorizedAdminRoleException : ApiException(message = "This resource requires administrative permissions") {
    override fun httpStatus(): Int {
        return HttpStatus.FORBIDDEN_403
    }

    override fun apiError(): ApiError {
        return ApiError.UNAUTHORIZED_ADMIN_ROLE_ERROR
    }

    override fun userResponseMessage(): String {
        return message!!
    }
}
