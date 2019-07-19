package resources.storage.entities

import org.jetbrains.exposed.sql.Table

const val VARCHAR_LENGTH = 255

object AnimalMap : Table("animals") {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", VARCHAR_LENGTH)
    val age = integer("age").nullable()
    val timeUnit = varchar("time_unit", VARCHAR_LENGTH).nullable()
    val specie = varchar("specie", VARCHAR_LENGTH).nullable()
    val description = varchar("description", VARCHAR_LENGTH)
    val creationDate = datetime("creation_date").nullable()
    val modificationDate = datetime("modification_date").nullable()
    val status = varchar("status", VARCHAR_LENGTH)
}