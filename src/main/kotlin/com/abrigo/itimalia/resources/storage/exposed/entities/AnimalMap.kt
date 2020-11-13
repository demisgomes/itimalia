package com.abrigo.itimalia.resources.storage.exposed.entities
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.jodatime.datetime

const val VARCHAR_LENGTH = 255

object AnimalMap : IntIdTable("animals") {
    val name = varchar("name", VARCHAR_LENGTH)
    val age = integer("age").nullable()
    val timeUnit = varchar("time_unit", VARCHAR_LENGTH).nullable()
    val specie = varchar("specie", VARCHAR_LENGTH).nullable()
    val description = varchar("description", VARCHAR_LENGTH)
    val creationDate = datetime("creation_date").nullable()
    val modificationDate = datetime("modification_date").nullable()
    val status = varchar("status", VARCHAR_LENGTH)
    val sex = varchar("sex", VARCHAR_LENGTH)
    val size =  varchar("size", VARCHAR_LENGTH)
    val castrated = bool("castrated")
    val createdById = reference("created_by_id", UserMap.id, ReferenceOption.NO_ACTION)
}