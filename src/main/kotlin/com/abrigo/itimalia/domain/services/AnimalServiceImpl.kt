package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.animal.Animal
import com.abrigo.itimalia.domain.entities.animal.AnimalRequest
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
    private val animalValidator: Validator<AnimalRequest>,
    private val userService: UserService
) : AnimalService {

    override fun getBySpecie(specie: Specie): List<Animal> {
        val allAnimals = animalRepository.getAll()
        return allAnimals.filter { it.specie == specie }
    }

    override fun getByName(name: String): List<Animal> {
        val allAnimals = animalRepository.getAll()
        return allAnimals.filter { it.name.contains(name) }
    }

    override fun getByStatus(animalStatus: AnimalStatus): List<Animal> {
        val allAnimals = animalRepository.getAll()
        return allAnimals.filter { it.status == animalStatus }
    }

    override fun getAll(): List<Animal> {
        return animalRepository.getAll()
    }

    override fun adopt(id: Int): Animal {
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

    override fun get(id: Int): Animal {
        return animalRepository.get(id)
    }

    override fun add(newAnimal: NewAnimalRequest, token: String): Animal {
        newAnimalValidator.validate(newAnimal)

        val userId = userService.getIdByToken(token)

        val animalToBeAddedDTO = Animal(
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

    private fun normalizeAge(animal: Animal): Animal {
        return if (animal.age == null && animal.timeUnit != null) {
            animal.copy(age = null, timeUnit = null)
        } else if (animal.age != null && animal.timeUnit == null) {
            animal.copy(timeUnit = TimeUnit.YEAR)
        } else animal
    }

    override fun update(id: Int, updatedAnimal: AnimalRequest): Animal {
        animalValidator.validate(updatedAnimal)
        val animalInDatabase = get(id)

        val animalToBeModifiedDTO = Animal(
            updatedAnimal.id,
            updatedAnimal.name!!,
            updatedAnimal.age,
            updatedAnimal.timeUnit,
            updatedAnimal.specie,
            updatedAnimal.description!!,
            animalInDatabase.creationDate,
            DateTime.now(),
            updatedAnimal.status,
            updatedAnimal.deficiencies,
            updatedAnimal.sex,
            updatedAnimal.size,
            updatedAnimal.castrated,
            updatedAnimal.createdById
        )
        return animalRepository.update(id, normalizeAge(animalToBeModifiedDTO))
    }

    override fun delete(id: Int) {
        return animalRepository.delete(id)
    }

}
