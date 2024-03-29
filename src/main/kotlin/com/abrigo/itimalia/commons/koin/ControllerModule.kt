package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.application.web.controllers.AdminController
import com.abrigo.itimalia.application.web.controllers.AnimalController
import com.abrigo.itimalia.application.web.controllers.AnimalImageController
import com.abrigo.itimalia.application.web.controllers.UserController
import org.koin.dsl.module

val controllerModule = module {
    single {
        UserController(
            get(),
            get()
        )
    }
    single { AdminController(get()) }
    single { AnimalController(get(), get(), get(), get()) }
    single { AnimalImageController(get(), get(), get()) }
}
