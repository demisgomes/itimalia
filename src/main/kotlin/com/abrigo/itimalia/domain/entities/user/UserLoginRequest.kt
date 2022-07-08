package com.abrigo.itimalia.domain.entities.user

import com.abrigo.itimalia.domain.validation.Request

data class UserLoginRequest(val email: String?, val password: String?) : Request

fun UserLoginRequest.toUserLogin() =
    UserLogin(email ?: "", password ?: "")
