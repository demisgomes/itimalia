package com.abrigo.itimalia.domain.exceptions

import org.eclipse.jetty.http.HttpStatus

private const val THIS_EMAIL_ALREADY_EXISTS = "This email already exists"

class EmailAlreadyExistsException : ApiException {

    constructor() : super(THIS_EMAIL_ALREADY_EXISTS)
    constructor(cause: Throwable) : super (message = THIS_EMAIL_ALREADY_EXISTS, cause)

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
