package com.abrigo.itimalia.domain.entities.animal

data class NewAnimalRequest(val name: String?, val age: Int?, val timeUnit : TimeUnit?, val specie: Specie?, val description: String?)

fun NewAnimalRequest.toNewAnimal() = NewAnimal(name!!, age, timeUnit, specie, description!!)