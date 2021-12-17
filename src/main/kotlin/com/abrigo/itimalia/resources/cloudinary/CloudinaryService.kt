package com.abrigo.itimalia.resources.cloudinary

import com.abrigo.itimalia.domain.services.ImageService
import com.cloudinary.Cloudinary
import com.cloudinary.EagerTransformation
import com.cloudinary.Transformation

class CloudinaryService(private val cloudinary: Cloudinary) : ImageService {
    override fun add(imageFiles: List<ByteArray>) : List<String> {
        return imageFiles.map { generateUrlImage(it) }.toList()
    }

    private fun generateUrlImage(byteArray: ByteArray): String {
        val uploadedImage = cloudinary.uploader().upload(byteArray, emptyMap<String, String>())
        return cloudinary.url().transformation(Transformation<EagerTransformation>().quality(60))
            .generate(uploadedImage["public_id"].toString() + "." + uploadedImage["format"]) ?: ""
    }
}