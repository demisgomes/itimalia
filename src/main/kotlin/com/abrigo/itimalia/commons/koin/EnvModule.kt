package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.application.config.EnvironmentConfig
import com.abrigo.itimalia.application.config.ItimaliaDotenv
import org.koin.dsl.module

val envModule = module {
    single { ItimaliaDotenv().build() }
    single { EnvironmentConfig(get()) }
}
