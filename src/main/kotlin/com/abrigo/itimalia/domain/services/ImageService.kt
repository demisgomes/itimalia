package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.image.Image
import com.abrigo.itimalia.domain.entities.image.ImageToBeUploaded

interface ImageService {
    fun add(imageFiles: List<ImageToBeUploaded>, animalId: Int) : List<Image>
}