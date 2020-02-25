package com.abrigo.itimalia.resources.validation.hibernate.entities

import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.Specie
import com.abrigo.itimalia.domain.entities.animal.TimeUnit
import com.fasterxml.jackson.annotation.JsonFormat
import org.joda.time.DateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class AnimalDTORequestModel(
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
    val status: AnimalStatus?
)