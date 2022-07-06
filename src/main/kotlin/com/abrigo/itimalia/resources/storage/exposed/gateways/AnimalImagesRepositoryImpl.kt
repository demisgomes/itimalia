package com.abrigo.itimalia.resources.storage.exposed.gateways

import com.abrigo.itimalia.domain.entities.image.Image
import com.abrigo.itimalia.domain.exceptions.AnimalNotFoundException
import com.abrigo.itimalia.domain.repositories.AnimalImagesRepository
import com.abrigo.itimalia.resources.storage.exposed.entities.AnimalImageEntity
import com.abrigo.itimalia.resources.storage.exposed.entities.AnimalMap
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.transactions.transaction

class AnimalImagesRepositoryImpl : AnimalImagesRepository {
    override fun addAll(newImages: List<Image>, newAnimalId: Int) {
        try {
            transaction {
                newImages.forEach {
                    AnimalImageEntity.new {
                        name = it.name
                        path = it.path
                        format = it.format
                        bytes = it.bytes
                        animalId = EntityID(newAnimalId, AnimalMap)
                    }
                }
            }
        } catch (exception: ExposedSQLException) {
            throw AnimalNotFoundException()
        }
    }
}
