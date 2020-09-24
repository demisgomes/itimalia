package com.abrigo.itimalia.resources.storage.entities

import org.jetbrains.exposed.dao.id.IntIdTable


object AnimalToAnimalDeficiencyMap: IntIdTable("animal_to_deficiency"){
    val animalId = reference("animal_id", AnimalMap.id)
    val animalDeficiencyId = reference("animal_deficiency_id", AnimalDeficiencyMap.id)
}