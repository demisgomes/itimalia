package com.abrigo.itimalia.resources.validation.hibernate.entities

import com.abrigo.itimalia.domain.entities.animal.AnimalDeficiency
import com.abrigo.itimalia.domain.entities.animal.AnimalSex
import com.abrigo.itimalia.domain.entities.animal.AnimalSize
import com.abrigo.itimalia.domain.entities.animal.Specie
import com.abrigo.itimalia.domain.entities.animal.TimeUnit
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class NewAnimalRequestModel(
    @field:NotBlank(message = "please fill with a name")
    val name: String?,
    val age: Int?,
    val timeUnit: TimeUnit?,
    @field:NotNull(message = "please fill with a valid specie: cat or dog")
    val specie: Specie?,
    @field:NotNull(message = "please fill with a description")
    val description: String?,
    val deficiencies: List<AnimalDeficiency>?,
    @field:NotNull(message = "please fill with a sex: male or female")
    val sex: AnimalSex?,
    @field:NotNull(message = "please fill with a size: tiny, small, medium, or large")
    val size: AnimalSize?,
    @field:NotNull(message = "please fill castrated with true or false")
    val castrated: Boolean?
)
