package com.abrigo.itimalia.domain.entities.user

import org.joda.time.DateTime

data class UserRequest(
    var id: Int?,
    var email: String?,
    val password: String?,
    val birthDate: DateTime?,
    val gender: Gender?,
    val name: String?,
    val phone: String?,
    var role: UserRole?,
    val creationDate: DateTime?,
    val modificationDate: DateTime?,
    var token: String?
)

fun UserRequest.toUser() =
    User(
        id,
        email ?: "",
        password ?: "",
        birthDate ?: DateTime.now(),
        gender ?: Gender.NOT_DECLARED,
        name ?: "",
        phone ?: "",
        role ?: UserRole.USER,
        creationDate ?: DateTime.now(),
        modificationDate ?: DateTime.now(),
        token ?: ""
    )
