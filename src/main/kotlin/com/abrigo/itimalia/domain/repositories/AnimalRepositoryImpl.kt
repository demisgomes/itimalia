package com.abrigo.itimalia.domain.repositories

import com.abrigo.itimalia.domain.entities.animal.AnimalDTO
import com.abrigo.itimalia.domain.entities.animal.AnimalSex
import com.abrigo.itimalia.domain.entities.animal.AnimalSize
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.Specie
import com.abrigo.itimalia.domain.entities.animal.TimeUnit
import com.abrigo.itimalia.domain.exceptions.AnimalNotFoundException
import com.abrigo.itimalia.resources.storage.entities.AnimalMap
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime

class AnimalRepositoryImpl : AnimalRepository {
    override fun getAll(): List<AnimalDTO> {
        return transaction {
            (AnimalMap).selectAll().map { resultRow ->
                buildAnimalDTO(resultRow)
            }
        }
    }

    override fun get(id: Int): AnimalDTO {
        try {
            return transaction {
                (AnimalMap).select { AnimalMap.id eq id }.map { resultRow ->
                    buildAnimalDTO(resultRow)
                }.first()
            }
        } catch (exception: NoSuchElementException) {
            throw AnimalNotFoundException()
        }
    }

    private fun buildAnimalDTO(resultRow: ResultRow): AnimalDTO {
        return AnimalDTO(
            id = resultRow[AnimalMap.id],
            name = resultRow[AnimalMap.name],
            age = resultRow[AnimalMap.age],
            timeUnit = resultRow[AnimalMap.timeUnit]?.let { TimeUnit.valueOf(resultRow[AnimalMap.timeUnit]!!) },
            specie = resultRow[AnimalMap.specie]?.let { Specie.valueOf(resultRow[AnimalMap.specie]!!) },
            creationDate = resultRow[AnimalMap.creationDate],
            modificationDate = resultRow[AnimalMap.modificationDate],
            description = resultRow[AnimalMap.description],
            status = AnimalStatus.valueOf(resultRow[AnimalMap.status]),
            deficiencies = emptyList(),
            sex = resultRow[AnimalMap.sex].let { AnimalSex.valueOf(resultRow[AnimalMap.sex]) },
            size = resultRow[AnimalMap.size].let { AnimalSize.valueOf(resultRow[AnimalMap.size]) },
            castrated = resultRow[AnimalMap.castrated],
            createdById = resultRow[AnimalMap.createdById]
        )
    }

    private fun getByCreationDate(creationDate: DateTime): AnimalDTO {
        try {
            return transaction {
                (AnimalMap).select { AnimalMap.creationDate eq creationDate }.map { resultRow ->
                    buildAnimalDTO(resultRow)
                }.first()
            }
        } catch (exception: NoSuchElementException) {
            throw AnimalNotFoundException()
        }
    }

    override fun add(newAnimal: AnimalDTO): AnimalDTO {
        transaction {
            AnimalMap.insert {
                it[name] = newAnimal.name
                it[age] = newAnimal.age
                it[timeUnit] = newAnimal.timeUnit?.toString()
                it[specie] = newAnimal.specie?.toString()
                it[description] = newAnimal.description
                it[creationDate] = newAnimal.creationDate
                it[modificationDate] = newAnimal.modificationDate
                it[status] = newAnimal.status.toString()
                it[sex] = newAnimal.sex.toString()
                it[size] = newAnimal.size.toString()
                it[castrated] = newAnimal.castrated
                it[createdById] = newAnimal.createdById

            }
            commit()
        }
        return getByCreationDate(newAnimal.creationDate!!)
    }

    override fun update(id: Int, animalDTO: AnimalDTO): AnimalDTO {
        val result = transaction {
            (AnimalMap).update({ AnimalMap.id eq id }) { resultRow ->
                resultRow[name] = animalDTO.name
                resultRow[age] = animalDTO.age
                resultRow[timeUnit] = animalDTO.timeUnit?.toString()
                resultRow[specie] = animalDTO.specie?.toString()
                resultRow[modificationDate] = DateTime.now()
                resultRow[description] = animalDTO.description
                resultRow[status] = animalDTO.status.toString()
            }
        }
        result.let { res ->
            when (res) {
                0 -> throw AnimalNotFoundException()
                else -> return get(id)
            }
        }
    }

    override fun delete(id: Int) {
        val result = transaction {
            AnimalMap.deleteWhere { AnimalMap.id eq id }
        }

        if (result == 0) throw AnimalNotFoundException()
    }
}
