package domain.repositories

import domain.entities.AnimalDTO
import domain.entities.NewAnimal

interface AnimalRepository {
    fun get(id:Int):AnimalDTO
    fun add(newAnimal: NewAnimal): AnimalDTO
}
