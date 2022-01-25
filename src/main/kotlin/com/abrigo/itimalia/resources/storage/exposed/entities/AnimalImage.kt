package com.abrigo.itimalia.resources.storage.exposed.entities

import com.abrigo.itimalia.domain.entities.image.Image
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.jodatime.datetime

object AnimalImage : IntIdTable("animal_images") {
    val name = varchar("name", VARCHAR_LENGTH)
    val path = varchar("path", VARCHAR_LENGTH)
    val format = varchar("format", 40)
    val timestamp = datetime("timestamp").nullable()
    val bytes = long("bytes")
    val animalId = reference("animal_id", AnimalMap, ReferenceOption.CASCADE)
}

class AnimalImageEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<AnimalImageEntity>(AnimalImage)
    var name by AnimalImage.name
    var path by AnimalImage.path
    var format by AnimalImage.format
    var timestamp by AnimalImage.timestamp
    var bytes by AnimalImage.bytes
    var animalId by AnimalImage.animalId

    fun toAnimalImage() = Image(name, path, format, bytes)
}