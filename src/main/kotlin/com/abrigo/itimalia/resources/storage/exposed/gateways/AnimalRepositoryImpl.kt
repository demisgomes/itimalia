package com.abrigo.itimalia.resources.storage.exposed.gateways

import com.abrigo.itimalia.domain.entities.animal.AnimalDTO
import com.abrigo.itimalia.domain.entities.animal.AnimalDeficiency
import com.abrigo.itimalia.domain.entities.animal.AnimalSex
import com.abrigo.itimalia.domain.entities.animal.AnimalSize
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.Specie
import com.abrigo.itimalia.domain.entities.animal.TimeUnit
import com.abrigo.itimalia.domain.exceptions.AnimalNotFoundException
import com.abrigo.itimalia.domain.repositories.AnimalRepository
import com.abrigo.itimalia.resources.storage.exposed.entities.AnimalDeficiencyMap
import com.abrigo.itimalia.resources.storage.exposed.entities.AnimalMap
import com.abrigo.itimalia.resources.storage.exposed.entities.AnimalToAnimalDeficiencyMap
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
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
        val animalId = resultRow[AnimalMap.id].value
        val deficiencies = getDeficiencies(animalId)

        return AnimalDTO(
            id = resultRow[AnimalMap.id].value,
            name = resultRow[AnimalMap.name],
            age = resultRow[AnimalMap.age],
            timeUnit = resultRow[AnimalMap.timeUnit]?.let { TimeUnit.valueOf(resultRow[AnimalMap.timeUnit]!!) },
            specie = resultRow[AnimalMap.specie]?.let { Specie.valueOf(resultRow[AnimalMap.specie]!!) },
            creationDate = resultRow[AnimalMap.creationDate],
            modificationDate = resultRow[AnimalMap.modificationDate],
            description = resultRow[AnimalMap.description],
            status = AnimalStatus.valueOf(resultRow[AnimalMap.status]),
            deficiencies = deficiencies,
            sex = resultRow[AnimalMap.sex].let { AnimalSex.valueOf(resultRow[AnimalMap.sex]) },
            size = resultRow[AnimalMap.size].let { AnimalSize.valueOf(resultRow[AnimalMap.size]) },
            castrated = resultRow[AnimalMap.castrated],
            createdById = resultRow[AnimalMap.createdById].value
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

            val idsAnimalDeficiencies = getDeficienciesId(newAnimal.deficiencies)

            val idAnimal = insertAndGetId(newAnimal)

            addAnimalToDeficiencyMap(idsAnimalDeficiencies, idAnimal)

            commit()
        }
        return getByCreationDate(newAnimal.creationDate!!)

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

    private fun insertAndGetId(newAnimal: AnimalDTO): EntityID<Int> {
        return AnimalMap.insertAndGetId {
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
    }

    override fun update(id: Int, animalDTO: AnimalDTO): AnimalDTO {
        val result = transaction {
            removeAnimalToDeficiency(id)
            val idsAnimalDeficiencies = getDeficienciesId(animalDTO.deficiencies)
            addAnimalToDeficiencyMap(idsAnimalDeficiencies, EntityID(id, AnimalMap))

            (AnimalMap).update({ AnimalMap.id eq id }) { resultRow ->
                resultRow[name] = animalDTO.name
                resultRow[age] = animalDTO.age
                resultRow[timeUnit] = animalDTO.timeUnit?.toString()
                resultRow[specie] = animalDTO.specie?.toString()
                resultRow[modificationDate] = DateTime.now()
                resultRow[description] = animalDTO.description
                resultRow[status] = animalDTO.status.toString()
                resultRow[sex] = animalDTO.sex.toString()
                resultRow[size] = animalDTO.size.toString()
                resultRow[castrated] = animalDTO.castrated
                resultRow[createdById] = animalDTO.createdById
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
}
