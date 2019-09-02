package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.domain.repositories.AnimalRepository
import com.abrigo.itimalia.domain.repositories.AnimalRepositoryImpl
import com.abrigo.itimalia.domain.repositories.UserRepository
import com.abrigo.itimalia.domain.repositories.UserRepositoryImpl
import org.koin.dsl.module.module

val repositoryModule = module {
    single{ UserRepositoryImpl() as UserRepository }
    single { AnimalRepositoryImpl() as AnimalRepository }
}