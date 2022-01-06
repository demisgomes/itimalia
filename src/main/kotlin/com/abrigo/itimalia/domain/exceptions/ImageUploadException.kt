package com.abrigo.itimalia.domain.exceptions

import org.eclipse.jetty.http.HttpStatus

class ImageUploadException(cause: Throwable) : ApiException(cause) {
    override fun httpStatus() = HttpStatus.BAD_GATEWAY_502

    override fun apiError() = ApiError.IMAGE_UPLOAD_ERROR

    override fun userResponseMessage() = cause?.message ?: "Error on upload image"

}
