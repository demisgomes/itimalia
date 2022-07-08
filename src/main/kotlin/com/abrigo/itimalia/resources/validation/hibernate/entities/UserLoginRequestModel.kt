package com.abrigo.itimalia.resources.validation.hibernate.entities

import com.abrigo.itimalia.resources.validation.hibernate.Model
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class UserLoginRequestModel(
    @field:Email(message = "please fill with an email following the pattern: email@email.com")
    @field:NotBlank(message = "please fill with an email")
    val email: String?,
    @field:NotBlank(message = "please fill with a password")
    val password: String?
) : Model
