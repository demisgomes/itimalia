package com.abrigo.itimalia.resources.cloudinary

import com.cloudinary.Cloudinary

object CloudinaryConfig {
    fun build() = Cloudinary(
        mapOf(
            "cloud_name" to System.getenv("CLOUDINARY_CLOUD_NAME"),
            "api_key" to System.getenv("CLOUDINARY_API_KEY"),
            "api_secret" to System.getenv("CLOUDINARY_API_SECRET")
        )
    )
}