package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.application.config.ItimaliaJsonSerializer
import com.abrigo.itimalia.application.config.RouteConfig
import com.abrigo.itimalia.application.web.handlers.ErrorHandler
import org.koin.dsl.module

val configModule = module {
    single { RouteConfig(get(), get(), get(), get()) }
    single { ItimaliaJsonSerializer.build() }
    single { ErrorHandler() }
}
