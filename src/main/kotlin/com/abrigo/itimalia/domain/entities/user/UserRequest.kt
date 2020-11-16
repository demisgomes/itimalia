package com.abrigo.itimalia.domain.entities.user

import org.joda.time.DateTime
import java.lang.IllegalArgumentException

data class UserRequest(
    var id: Int?,
    var email: String?,
    val password:String?,
    val birthDate: DateTime?,
    val gender: Gender?,
    val name: String?,
    val phone: String?,
    var role: UserRole?,
    val creationDate: DateTime?,
    val modificationDate: DateTime?,
    var token:String?)

fun UserRequest.toUser() =
    User(id,
        email?: throw IllegalArgumentException(),
        password?: throw IllegalArgumentException(),
        birthDate ?: throw IllegalArgumentException(),
        gender ?: throw IllegalArgumentException(),
        name?: throw IllegalArgumentException(),
        phone?: throw IllegalArgumentException(),
        role?: throw IllegalArgumentException(),
        creationDate ?: throw IllegalArgumentException(),
        modificationDate ?: throw IllegalArgumentException(),
        token ?: throw IllegalArgumentException())
