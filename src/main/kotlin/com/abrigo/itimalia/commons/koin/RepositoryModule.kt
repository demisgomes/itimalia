package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.domain.repositories.AnimalRepository
import com.abrigo.itimalia.domain.repositories.UserRepository
import com.abrigo.itimalia.resources.storage.exposed.gateways.AnimalRepositoryImpl
import com.abrigo.itimalia.resources.storage.exposed.gateways.UserRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single { UserRepositoryImpl() as UserRepository }
    single { AnimalRepositoryImpl(get()) as AnimalRepository }
}
