package com.abrigo.itimalia.resources.validation.hibernate.entities

import com.abrigo.itimalia.domain.entities.animal.AnimalDeficiency
import com.abrigo.itimalia.domain.entities.animal.AnimalSex
import com.abrigo.itimalia.domain.entities.animal.AnimalSize
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.Specie
import com.abrigo.itimalia.domain.entities.animal.TimeUnit
import com.fasterxml.jackson.annotation.JsonFormat
import org.joda.time.DateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class AnimalRequestModel(
    val id: Int?,
    @field:NotBlank(message = "please fill with a name")
    val name: String?,
    val age: Int?,
    val timeUnit : TimeUnit?,
    @field:NotNull(message = "please fill with a valid specie: cat or dog")
    val specie: Specie?,
    @field:NotNull(message = "please fill with a description")
    val description: String?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    val creationDate: DateTime?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    val modificationDate: DateTime?,
    @field:NotNull(message = "please fill with a status: gone, dead, adopted, or available")
    val status: AnimalStatus?,
    val deficiencies: List<AnimalDeficiency>?,
    @field:NotNull(message = "please fill with a sex: male or female")
    val sex: AnimalSex?,
    @field:NotNull(message = "please fill with a size: tiny, small, medium, or large")
    val size: AnimalSize?,
    @field:NotNull(message = "please fill castrated with true or false")
    val castrated: Boolean?,
    val createdById: Int
)