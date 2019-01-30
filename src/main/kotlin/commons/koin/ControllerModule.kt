package commons.koin

import application.web.controllers.UserController
import org.koin.dsl.module.module

val controllerModule= module {
    single { UserController(get()) }
}