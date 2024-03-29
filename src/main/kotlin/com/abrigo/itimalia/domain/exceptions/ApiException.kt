package com.abrigo.itimalia.domain.exceptions

abstract class ApiException : Exception {
    constructor(cause: Throwable) : super(cause)
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)

    abstract fun httpStatus(): Int
    abstract fun apiError(): ApiError
    abstract fun userResponseMessage(): String

    open fun details() = emptyMap<String, MutableList<String>>()

    fun createErrorResponse(): ErrorResponse {
        return ErrorResponse(
            apiError(),
            userResponseMessage(),
            details()
        )
    }
}
