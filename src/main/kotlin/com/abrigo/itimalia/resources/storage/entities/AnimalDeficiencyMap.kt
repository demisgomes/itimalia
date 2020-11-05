package com.abrigo.itimalia.resources.storage.entities

import org.jetbrains.exposed.dao.id.IntIdTable

object AnimalDeficiencyMap : IntIdTable("animal_deficiency"){
    val name = varchar("name", 100)
}