package domain.services

import domain.entities.AnimalDTO
import domain.entities.AnimalStatus
import domain.entities.NewAnimal
import domain.repositories.AnimalRepository
import domain.validation.AnimalValidation
import java.util.*

class AnimalServiceImpl(private val animalRepository: AnimalRepository) {
    fun get(id: Int): AnimalDTO {
        return animalRepository.get(id)
    }

    fun add(newAnimal: NewAnimal): AnimalDTO {
        var animalToBeAddedDTO = AnimalDTO(
            newAnimal.name,
            newAnimal.age,
            newAnimal.timeUnit,
            newAnimal.specie,
            newAnimal.description,
            Calendar.getInstance().time,
            Calendar.getInstance().time,
            AnimalStatus.AVAILABLE
        )

        animalToBeAddedDTO=AnimalValidation().validate(animalToBeAddedDTO)
        return animalRepository.add(animalToBeAddedDTO)
    }

}
