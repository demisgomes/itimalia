package com.abrigo.itimalia.domain.entities.user

import com.abrigo.itimalia.domain.entities.Gender
import com.fasterxml.jackson.annotation.JsonFormat
import org.joda.time.DateTime
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class NewUser(
    @Email(message = "invalid email")
    @NotNull(message = "invalid email")
    val email: String,
    val password:String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val birthDate: DateTime?,
    val gender: Gender?,
    @Size(min = 5, max = 50)
    val name: String,
    val phone: String)