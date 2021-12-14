package com.abrigo.itimalia.domain.services

import com.abrigo.itimalia.domain.entities.image.Image

interface ImageService {
    fun add(image: Image)
}