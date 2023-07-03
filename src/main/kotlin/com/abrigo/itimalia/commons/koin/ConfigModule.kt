package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.application.config.ItimaliaJsonSerializer
import com.abrigo.itimalia.application.web.handlers.ErrorHandler
import org.koin.dsl.module

val configModule = module {
    single { ItimaliaJsonSerializer.build() }
    single { ErrorHandler() }
}
