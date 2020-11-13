package com.abrigo.itimalia.resources.storage.exposed.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.jodatime.datetime

object UserMap: IntIdTable("users") {
    val email = varchar("email", VARCHAR_LENGTH)
    val password = varchar("password", VARCHAR_LENGTH)
    val birthDate = datetime("birth_date").nullable()
    val gender = varchar("gender", VARCHAR_LENGTH)
    val name = varchar("name", VARCHAR_LENGTH)
    val phone = varchar("phone", VARCHAR_LENGTH)
    val role = varchar("role", VARCHAR_LENGTH)
    val creationDate = datetime("creation_date").nullable()
    val modificationDate = datetime("modification_date").nullable()
    val token = varchar("token", VARCHAR_LENGTH)
}