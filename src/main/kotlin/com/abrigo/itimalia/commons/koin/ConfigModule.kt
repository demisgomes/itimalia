package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.application.config.ItimaliaJsonSerializer
import com.abrigo.itimalia.application.config.RouteConfig
import org.koin.dsl.module.module

val configModule=module{
    single { RouteConfig(get(), get(), get()) }
    single { ItimaliaJsonSerializer.build() }
}