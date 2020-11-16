package com.abrigo.itimalia.domain.entities.user

import java.lang.IllegalArgumentException

data class UserLoginRequest(val email: String?, val password: String?)

fun UserLoginRequest.toUserLogin() =
    UserLogin(email ?: throw IllegalArgumentException(), password ?: throw IllegalArgumentException())