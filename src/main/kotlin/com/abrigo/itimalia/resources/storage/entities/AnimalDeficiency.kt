package com.abrigo.itimalia.resources.storage.entities

import org.jetbrains.exposed.dao.IntIdTable

object AnimalDeficiency : IntIdTable("animal_deficiency"){
    val description = varchar("description", 255)
}