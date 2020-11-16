package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.application.web.controllers.AdminController
import com.abrigo.itimalia.application.web.controllers.AnimalController
import com.abrigo.itimalia.application.web.controllers.UserController
import org.koin.dsl.module.module


val controllerModule = module {
    single {
        UserController(
            get(),
            get()
        )
    }
    single { AdminController(get(),get()) }
    single { AnimalController(get(), get(), get()) }
}