package com.abrigo.itimalia.resources.validation.hibernate.entities

import com.abrigo.itimalia.domain.entities.animal.Specie
import com.abrigo.itimalia.domain.entities.animal.TimeUnit
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class NewAnimalRequestModel(
    @field:NotBlank(message = "please fill with a name")
    val name: String?,
    val age: Int?,
    val timeUnit : TimeUnit?,
    @field:NotNull(message = "please fill with a valid specie: cat or dog")
    val specie: Specie?,
    @field:NotNull(message = "please fill with a description")
    val description: String?
)