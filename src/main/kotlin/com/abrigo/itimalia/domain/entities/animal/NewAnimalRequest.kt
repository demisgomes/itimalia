package com.abrigo.itimalia.domain.entities.animal

import java.lang.IllegalArgumentException

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
)

fun NewAnimalRequest.toNewAnimal() =
    NewAnimal(
        name ?: throw IllegalArgumentException(),
        age,
        timeUnit,
        specie ?: throw IllegalArgumentException(),
        description ?: throw IllegalArgumentException(),
        deficiencies ?: throw IllegalArgumentException(),
        sex ?: throw IllegalArgumentException(),
        size ?: throw IllegalArgumentException(),
        castrated ?: throw IllegalArgumentException()
    )