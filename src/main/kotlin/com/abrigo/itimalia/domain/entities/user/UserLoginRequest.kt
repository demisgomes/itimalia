package com.abrigo.itimalia.domain.entities.user

data class UserLoginRequest(val email:String?, val password:String?)

fun UserLoginRequest.toUserLogin() = UserLogin(email!!, password!!)