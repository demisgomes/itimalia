package domain.repositories

import domain.entities.AnimalDTO
import domain.entities.AnimalStatus
import domain.entities.Specie
import domain.entities.TimeUnit
import domain.exceptions.AnimalNotFoundException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import resources.storage.entities.AnimalMap

class AnimalRepositoryImpl:AnimalRepository{
    override fun getAll(): List<AnimalDTO> {
        return transaction {
            (AnimalMap).selectAll().map { resultRow ->
                buildAnimalDTO(resultRow)
            }
        }
    }

    override fun get(id: Int): AnimalDTO {
        try{
            return transaction {
                (AnimalMap).select{ AnimalMap.id eq id }.map { resultRow ->
                    buildAnimalDTO(resultRow)
                }.first()
            }
        }
        catch (exception:NoSuchElementException){
            throw AnimalNotFoundException()
        }
    }

    private fun buildAnimalDTO(resultRow: ResultRow): AnimalDTO {
        return AnimalDTO(
            name = resultRow[AnimalMap.name],
            age = resultRow[AnimalMap.age],
            timeUnit = resultRow[AnimalMap.timeUnit]?.let { TimeUnit.valueOf(resultRow[AnimalMap.timeUnit]!!) },
            specie = resultRow[AnimalMap.specie]?.let { Specie.valueOf(resultRow[AnimalMap.specie]!!) },
            creationDate = resultRow[AnimalMap.creationDate],
            modificationDate = resultRow[AnimalMap.modificationDate],
            description = resultRow[AnimalMap.description],
            status = AnimalStatus.valueOf(resultRow[AnimalMap.status])
        )
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

            }
            commit()
        }
        return newAnimal
    }

    override fun update(id: Int, animalDTO: AnimalDTO): AnimalDTO {
        transaction {
            (AnimalMap).update({ AnimalMap.id eq id }) { resultRow ->
                resultRow[AnimalMap.name] = animalDTO.name
                resultRow[AnimalMap.age] = animalDTO.age
                resultRow[AnimalMap.timeUnit] = animalDTO.timeUnit?.toString()
                resultRow[AnimalMap.specie] = animalDTO.specie?.toString()
                resultRow[AnimalMap.modificationDate] = DateTime.now()
                resultRow[AnimalMap.description] = animalDTO.description
                resultRow[AnimalMap.status] = animalDTO.status.toString()
            }
        }
        return animalDTO
    }

    override fun delete(id: Int): AnimalDTO {
        val animalToBeRemoved=get(id)
        transaction {
            AnimalMap.deleteWhere { AnimalMap.id eq id }
        }

        return animalToBeRemoved
    }
}
