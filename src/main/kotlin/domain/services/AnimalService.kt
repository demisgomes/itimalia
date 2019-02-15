package domain.services

import domain.entities.AnimalDTO
import domain.entities.NewAnimal

interface AnimalService {
    fun add(newAnimal: NewAnimal): AnimalDTO
    fun get(id:Int): AnimalDTO
    fun update(id: Int, updatedAnimalDTO: AnimalDTO): AnimalDTO
    fun delete(id: Int): AnimalDTO

}
