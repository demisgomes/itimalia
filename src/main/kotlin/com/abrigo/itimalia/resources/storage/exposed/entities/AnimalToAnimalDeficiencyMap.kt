package com.abrigo.itimalia.resources.storage.exposed.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption


object AnimalToAnimalDeficiencyMap: IntIdTable("animal_to_deficiency"){
    val animalId = reference("animal_id", AnimalMap, ReferenceOption.CASCADE)
    val animalDeficiencyId = reference("animal_deficiency_id", AnimalDeficiencyMap, ReferenceOption.CASCADE)
}