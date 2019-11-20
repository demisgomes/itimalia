package com.abrigo.itimalia.application.web.handlers

import com.abrigo.itimalia.domain.exceptions.ApiError
import com.abrigo.itimalia.domain.exceptions.ApiException
import com.abrigo.itimalia.domain.exceptions.ErrorResponse
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import java.lang.Exception
import java.lang.IllegalArgumentException

class ErrorHandler{
    fun handle(exception: Exception, context: Context){
        if(exception is ApiException){
            context.json(exception.createErrorResponse())
            context.status(exception.httpStatus())
        }
        else if (exception is IllegalArgumentException){
            context.json(ErrorResponse(ApiError.BAD_REQUEST, "An error has occurred in request"))
        }
        else if (exception is BadRequestResponse){
            context.json(ErrorResponse(ApiError.BAD_REQUEST, exception.localizedMessage))
        }
    }
}