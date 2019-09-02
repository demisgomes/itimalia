package com.abrigo.itimalia.domain.exceptions

data class ErrorResponse(
    val apiError: ApiError,
    val message: String,
    val details: Map<String, List<String>>? = null
)