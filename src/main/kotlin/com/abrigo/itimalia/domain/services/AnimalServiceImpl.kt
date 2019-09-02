package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.AnimalDTO
import com.abrigo.itimalia.domain.entities.AnimalStatus
import com.abrigo.itimalia.domain.entities.NewAnimal
import com.abrigo.itimalia.domain.entities.Specie
import com.abrigo.itimalia.domain.exceptions.AnimalAlreadyAdoptedException
import com.abrigo.itimalia.domain.exceptions.AnimalDeadException
import com.abrigo.itimalia.domain.exceptions.AnimalGoneException
import com.abrigo.itimalia.domain.repositories.AnimalRepository
import com.abrigo.itimalia.domain.validation.AnimalValidation
import org.joda.time.DateTime

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
                    animalToBeAdopted.id,
                    animalToBeAdopted.name,
                    animalToBeAdopted.age,
                    animalToBeAdopted.timeUnit,
                    animalToBeAdopted.specie,
                    animalToBeAdopted.description,
                    animalToBeAdopted.creationDate,
                    DateTime.now(),
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
            null,
            newAnimal.name,
            newAnimal.age,
            newAnimal.timeUnit,
            newAnimal.specie,
            newAnimal.description,
            DateTime.now(),
            DateTime.now(),
            AnimalStatus.AVAILABLE
        )

        animalToBeAddedDTO=AnimalValidation().validate(animalToBeAddedDTO)
        return animalRepository.add(animalToBeAddedDTO)
    }

    override fun update(id:Int, updatedAnimalDTO: AnimalDTO): AnimalDTO {
        val animalInDatabase=get(id)
        var animalToBeModifiedDTO = AnimalDTO(
            updatedAnimalDTO.id,
            updatedAnimalDTO.name,
            updatedAnimalDTO.age,
            updatedAnimalDTO.timeUnit,
            updatedAnimalDTO.specie,
            updatedAnimalDTO.description,
            animalInDatabase.creationDate,
            DateTime.now(),
            updatedAnimalDTO.status
        )
        animalToBeModifiedDTO=AnimalValidation().validate(animalToBeModifiedDTO)
        return animalRepository.update(id,animalToBeModifiedDTO)
    }

    override fun delete(id: Int) {
        return animalRepository.delete(id)
    }

}
