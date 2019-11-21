package com.abrigo.itimalia.domain.entities.user

import com.abrigo.itimalia.domain.entities.Gender
import com.abrigo.itimalia.domain.entities.Roles
import com.fasterxml.jackson.annotation.JsonFormat
import org.joda.time.DateTime

class UserSearched(
    var id: Int?,
    var email: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    val birthDate: DateTime?,
    val gender: Gender?,
    val name: String,
    val phone: String,
    var role: Roles,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    val creationDate: DateTime?
)