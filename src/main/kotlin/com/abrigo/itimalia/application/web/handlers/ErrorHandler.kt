package com.abrigo.itimalia.application.web.handlers

import com.abrigo.itimalia.domain.exceptions.ApiError
import com.abrigo.itimalia.domain.exceptions.ApiException
import com.abrigo.itimalia.domain.exceptions.ErrorResponse
import io.javalin.http.Context

class ErrorHandler{
    fun handle(exception: Exception, context: Context){
        when (exception) {
            is ApiException -> {
                context.json(exception.createErrorResponse())
                context.status(exception.httpStatus())
            }
            is IllegalArgumentException -> context.json(ErrorResponse(ApiError.BAD_REQUEST, "An error has occurred in request"))
        }
    }
}