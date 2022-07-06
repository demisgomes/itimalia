package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.domain.repositories.AnimalImagesRepository
import com.abrigo.itimalia.domain.services.ImageService
import com.abrigo.itimalia.resources.images.cloudinary.CloudinaryConfig
import com.abrigo.itimalia.resources.images.cloudinary.gateways.CloudinaryService
import com.abrigo.itimalia.resources.storage.exposed.gateways.AnimalImagesRepositoryImpl
import org.koin.dsl.module.module

val imageModule = module {
    single {
        CloudinaryConfig.build()
    }
    single { CloudinaryService(get(), get()) as ImageService }
    single { AnimalImagesRepositoryImpl() as AnimalImagesRepository }
}
