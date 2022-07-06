package com.abrigo.itimalia.domain.exceptions

import org.eclipse.jetty.http.HttpStatus

class ImageUploadException(cause: Throwable, val fileNamesWithErrors: List<String> = emptyList()) : ApiException(cause) {
    override fun httpStatus() = HttpStatus.BAD_REQUEST_400

    override fun apiError() = ApiError.IMAGE_UPLOAD_ERROR

    override fun userResponseMessage() = cause?.message ?: "Error on upload image"

    override fun details(): Map<String, MutableList<String>> {
        if (fileNamesWithErrors.isEmpty()) return super.details()

        return mapOf("fileNamesWithErrors" to fileNamesWithErrors.toMutableList())
    }
}
