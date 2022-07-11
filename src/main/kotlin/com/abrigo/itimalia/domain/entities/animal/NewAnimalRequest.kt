package com.abrigo.itimalia.domain.entities.animal

import com.abrigo.itimalia.domain.validation.Request

data class NewAnimalRequest(
    val name: String?,
    val age: Int?,
    val timeUnit: TimeUnit?,
    val specie: Specie?,
    val description: String?,
    val deficiencies: List<AnimalDeficiency>?,
    val sex: AnimalSex?,
    val size: AnimalSize?,
    val castrated: Boolean?
) : Request
