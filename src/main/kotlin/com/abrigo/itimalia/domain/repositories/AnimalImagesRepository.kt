package com.abrigo.itimalia.domain.repositories

import com.abrigo.itimalia.domain.entities.image.Image

interface AnimalImagesRepository {
    fun add(newImage: Image, newAnimalId: Int)
    fun addAll(newImages: List<Image>, newAnimalId: Int)
}