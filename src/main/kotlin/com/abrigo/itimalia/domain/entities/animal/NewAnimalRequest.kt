package com.abrigo.itimalia.domain.entities.animal

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
        name!!,
        age,
        timeUnit,
        specie,
        description!!,
        deficiencies!!,
        sex!!,
        size!!,
        castrated!!
    )