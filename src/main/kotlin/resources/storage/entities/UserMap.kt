package resources.storage.entities

import org.jetbrains.exposed.sql.Table

object UserMap: Table("users") {
    val id = integer("id").autoIncrement().primaryKey()
    val email = varchar("email", VARCHAR_LENGTH)
    val password = varchar("password", VARCHAR_LENGTH)
    val birthDate = datetime("birth_date").nullable()
    val gender = varchar("gender", VARCHAR_LENGTH)
    val name = varchar("name", VARCHAR_LENGTH)
    val phone = varchar("phone", VARCHAR_LENGTH)
    val role = varchar("role", VARCHAR_LENGTH)
    val creationDate = datetime("creation_date")
    val modificationDate = datetime("modification_date")
    val token = varchar("token", VARCHAR_LENGTH)
}