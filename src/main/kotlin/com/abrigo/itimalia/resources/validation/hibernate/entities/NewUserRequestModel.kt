package com.abrigo.itimalia.resources.validation.hibernate.entities

import com.abrigo.itimalia.domain.entities.user.Gender
import com.abrigo.itimalia.resources.validation.hibernate.Model
import com.abrigo.itimalia.resources.validation.hibernate.constraints.MinimumAge
import com.fasterxml.jackson.annotation.JsonFormat
import org.joda.time.DateTime
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class NewUserRequestModel(
    @field:Email(message = "please fill with an email following the pattern: email@email.com")
    @field:NotBlank(message = "please fill with an email")
    val email: String?,
    @field:NotBlank(message = "please fill with a password")
    val password: String?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @field:MinimumAge(message = "please fill a birthDate with pattern yyyy-MM-dd of a user with more than 13 years old")
    val birthDate: DateTime?,
    @field:NotNull(message = "please fill with a gender: female, male, or not_declared")
    val gender: Gender?,
    @field:NotBlank(message = "please fill with a name")
    val name: String?,
    @field:NotBlank(message = "please fill with a phone")
    val phone: String?
) : Model
