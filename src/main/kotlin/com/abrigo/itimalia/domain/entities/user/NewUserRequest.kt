package com.abrigo.itimalia.domain.entities.user

import org.joda.time.DateTime
import java.lang.IllegalArgumentException

data class NewUserRequest(
    val email: String?,
    val password: String?,
    val birthDate: DateTime?,
    val gender: Gender?,
    val name: String?,
    val phone: String?
)

fun NewUserRequest.toNewUser() = NewUser(
    email ?: throw IllegalArgumentException(),
    password ?: throw IllegalArgumentException(),
    birthDate ?: throw IllegalArgumentException(),
    gender ?: throw IllegalArgumentException(),
    name ?: throw IllegalArgumentException(),
    phone ?: throw IllegalArgumentException()
)