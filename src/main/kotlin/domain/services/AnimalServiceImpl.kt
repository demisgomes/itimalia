package domain.services

import domain.entities.AnimalDTO
import domain.entities.AnimalStatus
import domain.entities.NewAnimal
import domain.repositories.AnimalRepository
import domain.validation.AnimalValidation
import java.util.*

class AnimalServiceImpl(private val animalRepository: AnimalRepository):AnimalService {
    override fun get(id: Int): AnimalDTO {
        return animalRepository.get(id)
    }

    override fun add(newAnimal: NewAnimal): AnimalDTO {
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

    override fun update(id:Int, updatedAnimalDTO: AnimalDTO): AnimalDTO {
        val animalInDatabase=get(id)
        var animalToBeModifiedDTO = AnimalDTO(
            updatedAnimalDTO.name,
            updatedAnimalDTO.age,
            updatedAnimalDTO.timeUnit,
            updatedAnimalDTO.specie,
            updatedAnimalDTO.description,
            animalInDatabase.creationDate,
            Calendar.getInstance().time,
            updatedAnimalDTO.status
        )

        animalToBeModifiedDTO=AnimalValidation().validate(animalToBeModifiedDTO)
        return animalRepository.update(id,animalToBeModifiedDTO)
    }

    override fun delete(id: Int): AnimalDTO {
        get(id)
        return animalRepository.delete(id)
    }

}
