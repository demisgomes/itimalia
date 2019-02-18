package domain.entities

import java.util.*

data class AnimalDTO(val name: String, val age: Int?, val timeUnit : TimeUnit?, val specie: Specie?, val description: String, val creationDate: Date, val modificationDate: Date, val status: AnimalStatus)
