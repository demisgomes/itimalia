package com.abrigo.itimalia.domain.entities.user

import org.joda.time.DateTime

data class NewUser(
    val email: String,
    val password: String,
    val birthDate: DateTime,
    val gender: Gender,
    val name: String,
    val phone: String
)
