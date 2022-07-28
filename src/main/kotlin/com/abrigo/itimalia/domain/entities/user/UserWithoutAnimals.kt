package com.abrigo.itimalia.domain.entities.user

import org.joda.time.DateTime

data class UserWithoutAnimals(
    val id: Int?,
    val email: String,
    val birthDate: DateTime?,
    val gender: Gender?,
    val name: String,
    val phone: String,
    val role: UserRole,
    val creationDate: DateTime?
)
