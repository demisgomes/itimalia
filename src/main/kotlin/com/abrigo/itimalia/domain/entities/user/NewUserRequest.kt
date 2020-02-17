package com.abrigo.itimalia.domain.entities.user

import com.abrigo.itimalia.domain.entities.Gender
import org.joda.time.DateTime

data class NewUserRequest(
    val email: String?,
    val password:String?,
    val birthDate: DateTime?,
    val gender: Gender?,
    val name: String?,
    val phone: String?)