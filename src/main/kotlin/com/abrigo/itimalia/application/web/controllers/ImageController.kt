package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.domain.entities.image.ImageToBeUploaded
import com.abrigo.itimalia.domain.services.ImageService
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus

class ImageController(private val imageService: ImageService) {
    fun addImage(context: Context) {
        val imageFilesToBeUploaded = context
            .uploadedFiles("files")
            .map { ImageToBeUploaded(it.filename, it.content.readBytes()) }
            .toList()

        val uploadedImagesList = imageService.add(imageFilesToBeUploaded)
        context.json(uploadedImagesList).status(HttpStatus.CREATED_201)
    }
}