package com.abrigo.itimalia.resources.storage.exposed.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object AnimalDeficiencyMap : IntIdTable("animal_deficiency"){
    val name = varchar("name", 100)
}

class AnimalDeficiencyEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<AnimalDeficiencyEntity>(AnimalDeficiencyMap)
    var name by AnimalDeficiencyMap.name
}