package com.abrigo.itimalia.domain.entities.animal

class NewAnimal(
    val name: String,
    val age: Int?,
    val timeUnit: TimeUnit?,
    val specie: Specie,
    val description: String,
    val deficiencies: List<AnimalDeficiency>,
    val sex: AnimalSex,
    val size: AnimalSize,
    val castrated: Boolean
)
