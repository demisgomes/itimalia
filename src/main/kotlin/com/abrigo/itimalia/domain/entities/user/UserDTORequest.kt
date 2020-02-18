package com.abrigo.itimalia.domain.entities.user

import com.fasterxml.jackson.annotation.JsonFormat
import org.joda.time.DateTime

data class UserDTORequest(
    var id: Int?,
    var email: String?,
    val password:String?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val birthDate: DateTime?,
    val gender: Gender?,
    val name: String?,
    val phone: String?,
    var role: Roles?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    val creationDate: DateTime?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    val modificationDate: DateTime?,
    var token:String?)

fun UserDTORequest.toUserDTO() = UserDTO(id, email!!, password!!, birthDate, gender, name!!, phone!!, role!!, creationDate, modificationDate, token)
