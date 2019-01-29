package commons.koin

import application.web.controllers.ItimaliaController
import org.koin.dsl.module.module

val controllerModule= module {
    single { ItimaliaController(get()) }
}