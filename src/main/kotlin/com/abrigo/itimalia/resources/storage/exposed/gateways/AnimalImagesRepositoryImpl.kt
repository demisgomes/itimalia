package com.abrigo.itimalia.resources.storage.exposed.gateways

import com.abrigo.itimalia.domain.entities.image.Image
import com.abrigo.itimalia.domain.repositories.AnimalImagesRepository
import com.abrigo.itimalia.resources.storage.exposed.entities.AnimalImageEntity
import com.abrigo.itimalia.resources.storage.exposed.entities.AnimalMap
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class AnimalImagesRepositoryImpl : AnimalImagesRepository{
    override fun add(newImage: Image, newAnimalId: Int) {
        transaction {
           AnimalImageEntity.new {
               name = newImage.name
               path = newImage.path
               format = newImage.format
               bytes = newImage.bytes
               animalId = EntityID(newAnimalId, AnimalMap)
           }
       }
    }

    override fun addAll(newImages: List<Image>, newAnimalId: Int) {
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
    }
}