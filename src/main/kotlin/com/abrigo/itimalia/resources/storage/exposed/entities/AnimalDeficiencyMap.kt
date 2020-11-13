package com.abrigo.itimalia.resources.storage.exposed.entities

import org.jetbrains.exposed.dao.id.IntIdTable

object AnimalDeficiencyMap : IntIdTable("animal_deficiency"){
    val name = varchar("name", 100)
}