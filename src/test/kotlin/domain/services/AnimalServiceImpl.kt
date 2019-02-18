package domain.services

import domain.entities.AnimalDTO
import domain.repositories.AnimalRepository

class AnimalServiceImpl(private val animalRepository: AnimalRepository) {
    fun get(id: Int): AnimalDTO {
        return animalRepository.get(id)
    }

}
