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

    fun update(id:Int, animalDTO: AnimalDTO): AnimalDTO {
        get(id)
        var animalToBeModifiedDTO = AnimalDTO(
            animalDTO.name,
            animalDTO.age,
            animalDTO.timeUnit,
            animalDTO.specie,
            animalDTO.description,
            animalDTO.creationDate,
            Calendar.getInstance().time,
            animalDTO.status
        )

        animalToBeModifiedDTO=AnimalValidation().validate(animalToBeModifiedDTO)
        return animalRepository.update(id,animalToBeModifiedDTO)
    }

}
