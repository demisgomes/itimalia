package com.abrigo.itimalia.resources.storage.exposed.gateways

import com.abrigo.itimalia.domain.entities.animal.Animal
import com.abrigo.itimalia.domain.entities.animal.AnimalDeficiency
import com.abrigo.itimalia.domain.entities.animal.AnimalSex
import com.abrigo.itimalia.domain.entities.animal.AnimalSize
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.Specie
import com.abrigo.itimalia.domain.entities.animal.TimeUnit
import com.abrigo.itimalia.domain.entities.user.toUserPublicInfo
import com.abrigo.itimalia.domain.exceptions.AnimalNotFoundException
import com.abrigo.itimalia.domain.repositories.AnimalRepository
import com.abrigo.itimalia.domain.repositories.UserRepository
import com.abrigo.itimalia.resources.storage.exposed.entities.AnimalDeficiencyEntity
import com.abrigo.itimalia.resources.storage.exposed.entities.AnimalDeficiencyMap
import com.abrigo.itimalia.resources.storage.exposed.entities.AnimalEntity
import com.abrigo.itimalia.resources.storage.exposed.entities.AnimalMap
import com.abrigo.itimalia.resources.storage.exposed.entities.AnimalToAnimalDeficiencyMap
import com.abrigo.itimalia.resources.storage.exposed.entities.UserEntity
import com.abrigo.itimalia.resources.storage.exposed.entities.UserMap
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime

class AnimalRepositoryImpl(private val userRepository: UserRepository) : AnimalRepository {
    override fun getAll(): List<Animal> {
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

    private fun buildAnimalDTO(resultRow: ResultRow): Animal {
        val animalId = resultRow[AnimalMap.id].value
        val deficiencies = getDeficiencies(animalId)

        return Animal(
            id = resultRow[AnimalMap.id].value,
            name = resultRow[AnimalMap.name],
            age = resultRow[AnimalMap.age],
            timeUnit = resultRow[AnimalMap.timeUnit]?.let { TimeUnit.valueOf(resultRow[AnimalMap.timeUnit]!!) },
            specie = resultRow[AnimalMap.specie].let { Specie.valueOf(resultRow[AnimalMap.specie]) },
            creationDate = resultRow[AnimalMap.creationDate],
            modificationDate = resultRow[AnimalMap.modificationDate],
            description = resultRow[AnimalMap.description],
            status = AnimalStatus.valueOf(resultRow[AnimalMap.status]),
            deficiencies = deficiencies,
            sex = resultRow[AnimalMap.sex].let { AnimalSex.valueOf(resultRow[AnimalMap.sex]) },
            size = resultRow[AnimalMap.size].let { AnimalSize.valueOf(resultRow[AnimalMap.size]) },
            castrated = resultRow[AnimalMap.castrated],
            createdById = resultRow[AnimalMap.createdById].value,
            adopterUser = resultRow[AnimalMap.adopterUser]?.let { UserEntity(it) }?.toUserPublicInfo()
        )
    }

    private fun getDeficiencies(animalId: Int): List<AnimalDeficiency> {
        return transaction {
            (AnimalDeficiencyMap innerJoin AnimalToAnimalDeficiencyMap).slice(AnimalDeficiencyMap.name).select {
                (AnimalToAnimalDeficiencyMap.animalId eq animalId) and (AnimalDeficiencyMap.id eq AnimalToAnimalDeficiencyMap.animalDeficiencyId)
            }.map { resultRow ->
                buildAnimalDeficiency(resultRow)
            }
        }
    }

    private fun buildAnimalDeficiency(resultRow: ResultRow): AnimalDeficiency {
        return AnimalDeficiency.valueOf(resultRow[AnimalDeficiencyMap.name])
    }

    override fun add(newAnimal: Animal): Animal {
        val deficienciesEntities = transaction {
            AnimalDeficiencyEntity.find { AnimalDeficiencyMap.name inList newAnimal.deficiencies.map { animalDeficiency -> animalDeficiency.toString() } }
        }

        val addedAnimalEntity = insertAnimalEntity(newAnimal)

        transaction {
            addedAnimalEntity.deficiencies = deficienciesEntities
        }

        return transaction {
            addedAnimalEntity.toAnimal()
        }

    }

    private fun addAnimalToDeficiencyMap(
        idsAnimalDeficiencies: List<EntityID<Int>>,
        idAnimal: EntityID<Int>
    ) {
        AnimalToAnimalDeficiencyMap.batchInsert(idsAnimalDeficiencies) { deficiencyId ->
            this[AnimalToAnimalDeficiencyMap.animalId] = idAnimal
            this[AnimalToAnimalDeficiencyMap.animalDeficiencyId] = deficiencyId
        }
    }

    private fun getDeficienciesId(deficiencies: List<AnimalDeficiency>): List<EntityID<Int>> {
        return (AnimalDeficiencyMap).select {
            AnimalDeficiencyMap.name inList deficiencies.map { animalDeficiency -> animalDeficiency.name }
        }.map { resultRow ->
            resultRow[AnimalDeficiencyMap.id]
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
            removeAnimalToDeficiency(id)
            val idsAnimalDeficiencies = getDeficienciesId(animal.deficiencies)
            addAnimalToDeficiencyMap(idsAnimalDeficiencies, EntityID(id, AnimalMap))

            (AnimalMap).update({ AnimalMap.id eq id }) { resultRow ->
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
                resultRow[adopterUser] = animal.adopterUser?.let { EntityID(animal.adopterUser.id ?: throw IllegalArgumentException("id from adopter not found"), UserMap) }
            }
        }
        result.let { res ->
            when (res) {
                0 -> throw AnimalNotFoundException()
                else -> return get(id)
            }
        }
    }

    private fun removeAnimalToDeficiency(id: Int) {
        AnimalToAnimalDeficiencyMap.deleteWhere {
            AnimalToAnimalDeficiencyMap.animalId eq id
        }
    }

    override fun delete(id: Int) {
        val result = transaction {
            removeAnimalToDeficiency(id)
            AnimalMap.deleteWhere { AnimalMap.id eq id }
        }

        if (result == 0) throw AnimalNotFoundException()
    }

    override fun adopt(animal: Animal, adopterId: Int): Animal {
        val adopterUser = userRepository.get(adopterId)
        val adoptedAnimal = animal.copy(status = AnimalStatus.ADOPTED, modificationDate = DateTime.now(), adopterUser = adopterUser.toUserPublicInfo())

        return update(animal.id ?: throw IllegalArgumentException(), adoptedAnimal)

    }
}
