package commons.koin

import application.web.controllers.AdminController
import application.web.controllers.AnimalController
import application.web.controllers.UserController
import org.koin.dsl.module.module

val controllerModule= module {
    single { UserController(get(), get()) }
    single { AdminController(get(),get()) }
    single { AnimalController(get()) }
}