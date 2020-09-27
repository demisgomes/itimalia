package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.animal.AnimalDTO
import com.abrigo.itimalia.domain.entities.animal.AnimalDTORequest
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.NewAnimalRequest
import com.abrigo.itimalia.domain.entities.animal.Specie
import com.abrigo.itimalia.domain.entities.animal.TimeUnit
import com.abrigo.itimalia.domain.exceptions.AnimalAlreadyAdoptedException
import com.abrigo.itimalia.domain.exceptions.AnimalDeadException
import com.abrigo.itimalia.domain.exceptions.AnimalGoneException
import com.abrigo.itimalia.domain.repositories.AnimalRepository
import com.abrigo.itimalia.domain.validation.Validator
import org.joda.time.DateTime

class AnimalServiceImpl(
    private val animalRepository: AnimalRepository,
    private val newAnimalValidator: Validator<NewAnimalRequest>,
    private val animalDTOValidator: Validator<AnimalDTORequest>,
    private val userService: UserService
) : AnimalService {

    override fun getBySpecie(specie: Specie): List<AnimalDTO> {
        val allAnimals = animalRepository.getAll()
        return allAnimals.filter { it.specie == specie }
    }

    override fun getByName(name: String): List<AnimalDTO> {
        val allAnimals = animalRepository.getAll()
        return allAnimals.filter { it.name.contains(name) }
    }

    override fun getByStatus(animalStatus: AnimalStatus): List<AnimalDTO> {
        val allAnimals = animalRepository.getAll()
        return allAnimals.filter { it.status == animalStatus }
    }

    override fun getAll(): List<AnimalDTO> {
        return animalRepository.getAll()
    }

    override fun adopt(id: Int): AnimalDTO {
        val animalToBeAdopted = get(id)
        when {
            animalToBeAdopted.status == AnimalStatus.AVAILABLE -> {
                val animalAdopted =
                    animalToBeAdopted.copy(modificationDate = DateTime.now(), status = AnimalStatus.ADOPTED)
                return animalRepository.update(id, animalAdopted)
            }
            animalToBeAdopted.status == AnimalStatus.ADOPTED -> throw AnimalAlreadyAdoptedException()
            animalToBeAdopted.status == AnimalStatus.DEAD -> throw AnimalDeadException()
            else -> throw AnimalGoneException()
        }

    }

    override fun get(id: Int): AnimalDTO {
        return animalRepository.get(id)
    }

    override fun add(newAnimal: NewAnimalRequest, token: String): AnimalDTO {
        newAnimalValidator.validate(newAnimal)

        val userId = userService.getIdByToken(token)

        val animalToBeAddedDTO = AnimalDTO(
            null,
            newAnimal.name!!,
            newAnimal.age,
            newAnimal.timeUnit,
            newAnimal.specie,
            newAnimal.description!!,
            DateTime.now(),
            DateTime.now(),
            AnimalStatus.AVAILABLE,
            newAnimal.deficiencies!!,
            newAnimal.sex!!,
            newAnimal.size!!,
            newAnimal.castrated!!,
            userId
        )
        return animalRepository.add(normalizeAge(animalToBeAddedDTO))
    }

    private fun normalizeAge(animalDTO: AnimalDTO): AnimalDTO {
        return if (animalDTO.age == null && animalDTO.timeUnit != null) {
            animalDTO.copy(age = null, timeUnit = null)
        } else if (animalDTO.age != null && animalDTO.timeUnit == null) {
            animalDTO.copy(timeUnit = TimeUnit.YEAR)
        } else animalDTO
    }

    override fun update(id: Int, updatedAnimalDTO: AnimalDTORequest): AnimalDTO {
        animalDTOValidator.validate(updatedAnimalDTO)
        val animalInDatabase = get(id)

        val animalToBeModifiedDTO = AnimalDTO(
            updatedAnimalDTO.id,
            updatedAnimalDTO.name!!,
            updatedAnimalDTO.age,
            updatedAnimalDTO.timeUnit,
            updatedAnimalDTO.specie,
            updatedAnimalDTO.description!!,
            animalInDatabase.creationDate,
            DateTime.now(),
            updatedAnimalDTO.status,
            updatedAnimalDTO.deficiencies,
            updatedAnimalDTO.sex,
            updatedAnimalDTO.size,
            updatedAnimalDTO.castrated,
            updatedAnimalDTO.createdById
        )
        return animalRepository.update(id, normalizeAge(animalToBeModifiedDTO))
    }

    override fun delete(id: Int) {
        return animalRepository.delete(id)
    }

}
