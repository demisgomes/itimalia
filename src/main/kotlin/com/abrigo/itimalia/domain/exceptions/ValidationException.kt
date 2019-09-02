package com.abrigo.itimalia.domain.exceptions

import org.eclipse.jetty.http.HttpStatus

class ValidationException(
    val errorDetails: HashMap<String, MutableList<String>>):
    ApiException("The validation does not successful in following field(s): $errorDetails"){

    override fun apiError() = ApiError.VALIDATION_ERROR
    override fun userResponseMessage() = "Invalid fields: $errorDetails"
    override fun httpStatus() = HttpStatus.BAD_REQUEST_400
    override fun details() = errorDetails

}