package com.abrigo.itimalia.resources.storage.exposed.entities
import com.abrigo.itimalia.domain.entities.animal.Animal
import com.abrigo.itimalia.domain.entities.animal.AnimalDeficiency
import com.abrigo.itimalia.domain.entities.animal.AnimalSex
import com.abrigo.itimalia.domain.entities.animal.AnimalSize
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.AnimalWithoutAdopter
import com.abrigo.itimalia.domain.entities.animal.Specie
import com.abrigo.itimalia.domain.entities.animal.TimeUnit
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.jodatime.datetime

const val VARCHAR_LENGTH = 255

object AnimalMap : IntIdTable("animals") {
    val name = varchar("name", VARCHAR_LENGTH)
    val age = integer("age").nullable()
    val timeUnit = varchar("time_unit", VARCHAR_LENGTH).nullable()
    val specie = varchar("specie", VARCHAR_LENGTH)
    val description = varchar("description", VARCHAR_LENGTH)
    val creationDate = datetime("creation_date")
    val modificationDate = datetime("modification_date")
    val status = varchar("status", VARCHAR_LENGTH)
    val sex = varchar("sex", VARCHAR_LENGTH)
    val size =  varchar("size", VARCHAR_LENGTH)
    val castrated = bool("castrated")
    val createdById = reference("created_by_id", UserMap.id, ReferenceOption.NO_ACTION)
    val adopterUser = reference("adopter_user", UserMap).nullable()
}

class AnimalEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<AnimalEntity>(AnimalMap)
    var name by AnimalMap.name
    var age by AnimalMap.age
    var timeUnit by AnimalMap.timeUnit
    var specie by AnimalMap.specie
    var description by AnimalMap.description
    var creationDate by AnimalMap.creationDate
    var modificationDate by AnimalMap.modificationDate
    var status by AnimalMap.status
    var sex by AnimalMap.sex
    var size by AnimalMap.size
    var castrated by AnimalMap.castrated
    var createdById by AnimalMap.createdById
    var adopterUser by UserEntity optionalReferencedOn AnimalMap.adopterUser
    var deficiencies by AnimalDeficiencyEntity via AnimalToAnimalDeficiencyMap

    fun toAnimal() =
        Animal(
            id.value,
            name,
            age,
            timeUnit?.let { TimeUnit.valueOf(it) },
            Specie.valueOf(specie),
            description,
            creationDate,
            modificationDate,
            AnimalStatus.valueOf(status),
            deficiencies.map { animalDeficiencyEntity -> AnimalDeficiency.valueOf(animalDeficiencyEntity.name) },
            AnimalSex.valueOf(sex),
            AnimalSize.valueOf(size),
            castrated,
            createdById.value,
            adopterUser?.toUserPublicInfo()
            )

    fun toAnimalWithoutAdopter() =
        AnimalWithoutAdopter(
            id.value,
            name,
            age,
            timeUnit?.let { TimeUnit.valueOf(it) },
            Specie.valueOf(specie),
            description,
            creationDate,
            modificationDate,
            AnimalStatus.valueOf(status),
            deficiencies.map { animalDeficiencyEntity -> AnimalDeficiency.valueOf(animalDeficiencyEntity.name) },
            AnimalSex.valueOf(sex),
            AnimalSize.valueOf(size),
            castrated,
            createdById.value
        )

}