package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.application.config.EnvironmentConfig
import com.abrigo.itimalia.domain.entities.image.ImageToBeUploaded
import com.abrigo.itimalia.domain.exceptions.ImageUploadException
import com.abrigo.itimalia.domain.services.ImageService
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URLConnection
import kotlin.streams.toList


class ImageController(private val imageService: ImageService) {
    fun addImage(context: Context) {
        val imageFilesToBeUploaded = context
            .uploadedFiles("files")
            .map { ImageToBeUploaded(it.filename, it.content.readBytes()) }
            .toList()

        val animalId = context.pathParam("id").toInt()

        checkIfIsEmpty(imageFilesToBeUploaded)

        checkIfImagesSurpassMaxSize(context)

        checkIfAreImages(imageFilesToBeUploaded)

        val uploadedImagesList = imageService.add(imageFilesToBeUploaded, animalId)
        context.json(uploadedImagesList).status(HttpStatus.CREATED_201)
    }

    private fun checkIfAreImages(imageFilesToBeUploaded: List<ImageToBeUploaded>) {
        val filesNotImages = imageFilesToBeUploaded.stream().filter {
            !isImage(it.byteArray)
        }.map { it.fileName }.toList()

        if (filesNotImages.isNotEmpty()) throw ImageUploadException(
            Throwable("there are files that are not supported. Upload PNG, JPEG, or SVG."),
            filesNotImages
        )
    }

    private fun checkIfImagesSurpassMaxSize(context: Context) {
        val imagesBiggerThanLimit = context
            .uploadedFiles("files").stream().filter {
                it.size > EnvironmentConfig.maxFileSize().toInt()
            }.map { it.filename }.toList()

        if (imagesBiggerThanLimit.isNotEmpty()) throw ImageUploadException(
            Throwable("there are some images bigger than limit"),
            imagesBiggerThanLimit
        )
    }

    private fun checkIfIsEmpty(imageFilesToBeUploaded: List<ImageToBeUploaded>) {
        if (imageFilesToBeUploaded.isEmpty()) throw ImageUploadException(Throwable("There is no images to be uploaded"))
    }

    private fun isImage(byteArray: ByteArray): Boolean{
        val inputStream: InputStream = BufferedInputStream(ByteArrayInputStream(byteArray))
        val mimeType: String? = URLConnection.guessContentTypeFromStream(inputStream)
        inputStream.close()

        return mimeType?.startsWith("image/") ?: false
    }
}