package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.animal.Animal
import com.abrigo.itimalia.domain.entities.animal.AnimalRequest
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.NewAnimalRequest
import com.abrigo.itimalia.domain.entities.animal.Specie
import com.abrigo.itimalia.domain.entities.animal.TimeUnit
import com.abrigo.itimalia.domain.entities.filter.FilterOptions
import com.abrigo.itimalia.domain.exceptions.AnimalAlreadyAdoptedException
import com.abrigo.itimalia.domain.exceptions.AnimalDeadException
import com.abrigo.itimalia.domain.exceptions.AnimalGoneException
import com.abrigo.itimalia.domain.repositories.AnimalRepository
import com.abrigo.itimalia.domain.validation.Request
import com.abrigo.itimalia.domain.validation.ValidatorRequest
import org.joda.time.DateTime

class AnimalServiceImpl(
    private val animalRepository: AnimalRepository,
    private val validatorRequest: ValidatorRequest<Request>
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

    override fun getAll(filterOptions: FilterOptions): List<Animal> {
        return animalRepository.getAll(filterOptions)
    }

    override fun adopt(id: Int, adopterId: Int): Animal {
        val animalToBeAdopted = get(id)
        when (animalToBeAdopted.status) {
            AnimalStatus.AVAILABLE -> return animalRepository.adopt(animalToBeAdopted, adopterId)
            AnimalStatus.ADOPTED -> throw AnimalAlreadyAdoptedException()
            AnimalStatus.DEAD -> throw AnimalDeadException()
            else -> throw AnimalGoneException()
        }
    }

    override fun get(id: Int): Animal {
        return animalRepository.get(id)
    }

    override fun add(newAnimal: NewAnimalRequest, creatorId: Int): Animal {
        validatorRequest.validate(newAnimal)

        val animalToBeAddedDTO = Animal(
            null,
            newAnimal.name ?: throw IllegalArgumentException(),
            newAnimal.age,
            newAnimal.timeUnit,
            newAnimal.specie ?: throw IllegalArgumentException(),
            newAnimal.description ?: throw IllegalArgumentException(),
            DateTime.now(),
            DateTime.now(),
            AnimalStatus.AVAILABLE,
            newAnimal.deficiencies ?: throw IllegalArgumentException(),
            newAnimal.sex ?: throw IllegalArgumentException(),
            newAnimal.size ?: throw IllegalArgumentException(),
            newAnimal.castrated ?: throw IllegalArgumentException(),
            creatorId
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
        validatorRequest.validate(updatedAnimal)
        val animalInDatabase = get(id)

        val animalToBeModifiedDTO = Animal(
            updatedAnimal.id,
            updatedAnimal.name ?: throw IllegalArgumentException(),
            updatedAnimal.age,
            updatedAnimal.timeUnit,
            updatedAnimal.specie ?: throw IllegalArgumentException(),
            updatedAnimal.description ?: throw IllegalArgumentException(),
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
