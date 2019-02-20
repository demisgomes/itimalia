package domain.services

import domain.entities.AnimalDTO
import domain.entities.AnimalStatus
import domain.entities.NewAnimal
import domain.entities.Specie
import domain.exceptions.AnimalAlreadyAdoptedException
import domain.exceptions.AnimalDeadException
import domain.exceptions.AnimalGoneException
import domain.repositories.AnimalRepository
import domain.validation.AnimalValidation
import java.util.*

class AnimalServiceImpl(private val animalRepository: AnimalRepository):AnimalService {
    override fun getBySpecie(specie: Specie): List<AnimalDTO>{
        val allAnimals=animalRepository.getAll()
        return allAnimals.filter { it.specie==specie }
    }

    override fun getByName(name: String): List<AnimalDTO> {
        val allAnimals=animalRepository.getAll()
        return allAnimals.filter { it.name.contains(name) }
    }

    override fun getByStatus(animalStatus: AnimalStatus): List<AnimalDTO> {
        val allAnimals=animalRepository.getAll()
        return allAnimals.filter { it.status==animalStatus }
    }

    override fun getAll(): List<AnimalDTO> {
        return animalRepository.getAll()
    }

    override fun adopt(id: Int): AnimalDTO {
        val animalToBeAdopted=get(id)
        when {
            animalToBeAdopted.status==AnimalStatus.AVAILABLE -> {
                val animalAdopted=AnimalDTO(
                    animalToBeAdopted.name,
                    animalToBeAdopted.age,
                    animalToBeAdopted.timeUnit,
                    animalToBeAdopted.specie,
                    animalToBeAdopted.description,
                    animalToBeAdopted.creationDate,
                    Calendar.getInstance().time,
                    AnimalStatus.ADOPTED
                )
                return animalRepository.update(id,animalAdopted)
            }
            animalToBeAdopted.status==AnimalStatus.ADOPTED -> throw AnimalAlreadyAdoptedException()
            animalToBeAdopted.status==AnimalStatus.DEAD -> throw AnimalDeadException()
            else -> throw AnimalGoneException()
        }

    }

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
