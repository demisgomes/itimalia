package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.domain.services.*
import org.koin.dsl.module.module


val serviceModule = module{
    single{ UserServiceImpl(get(), get()) as UserService }
    single { AdminServiceImpl(get(), get ()) as AdminService }
    single { AnimalServiceImpl(get()) as AnimalService }
}