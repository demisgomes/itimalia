package com.abrigo.itimalia.domain.exceptions
import org.eclipse.jetty.http.HttpStatus

class UnauthorizedUserRoleException : ApiException(message = "This resource requires only user permissions. Did you logged in an administrative account?") {
    override fun httpStatus(): Int {
        return HttpStatus.FORBIDDEN_403
    }

    override fun apiError(): ApiError {
        return ApiError.UNAUTHORIZED_USER_ROLE_ERROR
    }

    override fun userResponseMessage(): String {
        return message!!
    }
}
