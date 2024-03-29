package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.domain.services.AdminService
import com.abrigo.itimalia.domain.services.AdminServiceImpl
import com.abrigo.itimalia.domain.services.AnimalService
import com.abrigo.itimalia.domain.services.AnimalServiceImpl
import com.abrigo.itimalia.domain.services.UserService
import com.abrigo.itimalia.domain.services.UserServiceImpl
import org.koin.dsl.module

val serviceModule = module {
    single {
        UserServiceImpl(get(), get(), get(), get()) as UserService
    }
    single { AdminServiceImpl(get(), get(), get()) as AdminService }
    single {
        AnimalServiceImpl(get(), get()) as AnimalService
    }
}
