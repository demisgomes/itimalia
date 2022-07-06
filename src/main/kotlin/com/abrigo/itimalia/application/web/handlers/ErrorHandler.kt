package com.abrigo.itimalia.application.web.handlers

import com.abrigo.itimalia.domain.exceptions.ApiError
import com.abrigo.itimalia.domain.exceptions.ApiException
import com.abrigo.itimalia.domain.exceptions.ErrorResponse
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus

class ErrorHandler {
    fun handleApiException(exception: ApiException, context: Context) {
        context.json(exception.createErrorResponse())
        context.status(exception.httpStatus())
    }

    fun handleIllegalArgumentException(exception: IllegalArgumentException, context: Context) {
        context.json(ErrorResponse(ApiError.BAD_REQUEST, "An error has occurred in request", mapOf("cause" to listOf(exception.localizedMessage ?: ""))))
        context.status(HttpStatus.BAD_REQUEST_400)
    }

    fun handleBadRequestResponse(exception: BadRequestResponse, context: Context) {
        context.json(ErrorResponse(ApiError.BAD_REQUEST, exception.localizedMessage, emptyMap())).status(
            HttpStatus.BAD_REQUEST_400
        )
    }

    fun handleGenericException(exception: Exception, context: Context) {
        context.json(ErrorResponse(ApiError.INTERNAL_SERVER_ERROR, "Internal Server Error", mapOf("cause" to listOf<String>(exception.localizedMessage))))
        context.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
    }
}
