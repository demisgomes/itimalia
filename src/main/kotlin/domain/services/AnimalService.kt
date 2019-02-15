package domain.services

import domain.entities.AnimalDTO
import domain.entities.NewAnimal

interface AnimalService {
    fun add(newAnimal: NewAnimal): AnimalDTO

}
