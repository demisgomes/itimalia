package com.abrigo.itimalia.resources.images.cloudinary.gateways

import com.abrigo.itimalia.domain.entities.image.Image
import com.abrigo.itimalia.domain.entities.image.ImageToBeUploaded
import com.abrigo.itimalia.domain.exceptions.ImageUploadException
import com.abrigo.itimalia.domain.repositories.AnimalImagesRepository
import com.abrigo.itimalia.domain.services.ImageService
import com.cloudinary.Cloudinary
import com.cloudinary.EagerTransformation
import com.cloudinary.Transformation

class CloudinaryService(private val cloudinary: Cloudinary, private val animalImagesRepository: AnimalImagesRepository) : ImageService {
    override fun add(imageFiles: List<ImageToBeUploaded>, animalId: Int): List<Image> {
        val uploadedImages = imageFiles.map { uploadImage(it) }.toList()
        animalImagesRepository.addAll(uploadedImages, animalId)
        return uploadedImages
    }

    private fun uploadImage(imageToBeUploaded: ImageToBeUploaded): Image {
        try {
            val uploadedImage = cloudinary.uploader().upload(imageToBeUploaded.byteArray, emptyMap<String, String>())
            val uploadedImageFormat = uploadedImage["format"].toString()
            val uploadedImagePath = cloudinary.url().transformation(Transformation<EagerTransformation>().quality(60))
                .generate(uploadedImage["public_id"].toString() + "." + uploadedImageFormat)
                ?: uploadedImage["url"].toString()

            return Image(
                imageToBeUploaded.fileName,
                uploadedImagePath,
                uploadedImageFormat,
                uploadedImage["bytes"].toString().toLong()
            )
        } catch (exception: RuntimeException) {
            throw ImageUploadException(exception.cause ?: Throwable("Error on upload"))
        }
    }
}
