package com.abrigo.itimalia.resources.storage.exposed.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object AnimalToAdopterMap: IntIdTable("animal_to_adopter") {
    val animalId = reference("animal_id", AnimalMap.id, ReferenceOption.CASCADE)
    val userId = reference("user_id", UserMap.id)
}