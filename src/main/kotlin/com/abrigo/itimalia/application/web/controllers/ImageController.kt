package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.domain.services.ImageService
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus

class ImageController(private val imageService: ImageService) {
    fun addImage(context: Context) {
        val imageFiles = context.uploadedFiles("files").map { it.content.readBytes() }.toList()
        val uploadedImagesList = imageService.add(imageFiles)
        context.json(uploadedImagesList).status(HttpStatus.CREATED_201)
    }
}