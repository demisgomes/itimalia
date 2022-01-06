package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.domain.services.ImageService
import com.abrigo.itimalia.resources.images.cloudinary.CloudinaryConfig
import com.abrigo.itimalia.resources.images.cloudinary.gateways.CloudinaryService
import org.koin.dsl.module.module

val imageModule = module {
    single {
        CloudinaryConfig.build()
    }
    single { CloudinaryService(get()) as ImageService }
}