package com.abrigo.itimalia.resources.storage.exposed.gateways

import com.abrigo.itimalia.domain.entities.animal.Animal
import com.abrigo.itimalia.domain.entities.animal.AnimalDeficiency
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.filter.FilterOptions
import com.abrigo.itimalia.domain.entities.user.toUserPublicInfo
import com.abrigo.itimalia.domain.exceptions.AnimalNotFoundException
import com.abrigo.itimalia.domain.repositories.AnimalRepository
import com.abrigo.itimalia.domain.repositories.UserRepository
import com.abrigo.itimalia.resources.storage.exposed.entities.AnimalDeficiencyEntity
import com.abrigo.itimalia.resources.storage.exposed.entities.AnimalDeficiencyMap
import com.abrigo.itimalia.resources.storage.exposed.entities.AnimalEntity
import com.abrigo.itimalia.resources.storage.exposed.entities.AnimalMap
import com.abrigo.itimalia.resources.storage.exposed.entities.UserMap
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime

class AnimalRepositoryImpl(private val userRepository: UserRepository) : AnimalRepository {
    override fun getAll(filterOptions: FilterOptions): List<Animal> {
        val query = AnimalMap.selectAll()

        filterOptions.specie?.let { specie ->
            query.andWhere {
                AnimalMap.specie eq specie.name
            }
        }

        filterOptions.name?.let { name ->
            query.andWhere {
                AnimalMap.name like "%$name%"
            }
        }

        filterOptions.status?.let { status ->
            query.andWhere {
                AnimalMap.status eq status.name
            }
        }

        query.where?.let { where ->
            return transaction {
                AnimalEntity.find {
                    where
                }.map { animalEntity ->
                    animalEntity.toAnimal()
                }
            }
        }

        return transaction {
            AnimalEntity.all().map { animalEntity ->
                animalEntity.toAnimal()
            }
        }
    }

    override fun get(id: Int): Animal {
        try {
            return transaction {
                AnimalEntity[id].toAnimal()
            }
        } catch (exception: EntityNotFoundException) {
            throw AnimalNotFoundException()
        }
    }

    override fun add(newAnimal: Animal): Animal {
        val addedAnimalEntity = insertAnimalEntity(newAnimal)

        return populateAnimalWithDeficiencies(newAnimal.deficiencies, addedAnimalEntity)
    }

    private fun populateAnimalWithDeficiencies(
        deficiencies: List<AnimalDeficiency>,
        animalEntity: AnimalEntity
    ): Animal {
        val deficienciesEntities = transaction {
            AnimalDeficiencyEntity.find { AnimalDeficiencyMap.name inList deficiencies.map { animalDeficiency -> animalDeficiency.toString() } }
        }

        transaction {
            animalEntity.deficiencies = deficienciesEntities
        }

        return transaction {
            animalEntity.toAnimal()
        }
    }

    private fun insertAnimalEntity(newAnimal: Animal): AnimalEntity {
        return transaction {
            AnimalEntity.new {
                name = newAnimal.name
                age = newAnimal.age
                timeUnit = newAnimal.timeUnit?.toString()
                specie = newAnimal.specie.toString()
                description = newAnimal.description
                creationDate = newAnimal.creationDate
                modificationDate = newAnimal.modificationDate
                status = newAnimal.status.toString()
                sex = newAnimal.sex.toString()
                size = newAnimal.size.toString()
                castrated = newAnimal.castrated
                createdById = EntityID(newAnimal.createdById, UserMap)
            }
        }
    }

    override fun update(id: Int, animal: Animal): Animal {
        val result = transaction {
            AnimalMap.update({ AnimalMap.id eq id }) { resultRow ->
                resultRow[name] = animal.name
                resultRow[age] = animal.age
                resultRow[timeUnit] = animal.timeUnit?.toString()
                resultRow[specie] = animal.specie.toString()
                resultRow[modificationDate] = DateTime.now()
                resultRow[description] = animal.description
                resultRow[status] = animal.status.toString()
                resultRow[sex] = animal.sex.toString()
                resultRow[size] = animal.size.toString()
                resultRow[castrated] = animal.castrated
                resultRow[createdById] = animal.createdById
                resultRow[adoptedBy] = animal.adoptedBy?.let { EntityID(animal.adoptedBy.id ?: throw IllegalArgumentException("id from adopter not found"), UserMap) }
            }
        }
        result.let { res ->
            when (res) {
                0 -> throw AnimalNotFoundException()
                else -> {
                    val updatedAnimalEntity = transaction { AnimalEntity[id] }
                    return populateAnimalWithDeficiencies(animal.deficiencies, updatedAnimalEntity)
                }
            }
        }
    }

    override fun delete(id: Int) {
        val result = transaction {
            AnimalMap.deleteWhere { AnimalMap.id eq id }
        }

        if (result == 0) throw AnimalNotFoundException()
    }

    override fun adopt(animal: Animal, adopterId: Int): Animal {
        val adopterUser = userRepository.get(adopterId)
        val adoptedAnimal = animal.copy(status = AnimalStatus.ADOPTED, modificationDate = DateTime.now(), adoptedBy = adopterUser.toUserPublicInfo())

        return update(animal.id ?: throw IllegalArgumentException(), adoptedAnimal)
    }
}
