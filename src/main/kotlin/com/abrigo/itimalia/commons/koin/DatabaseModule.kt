package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.application.config.DatabaseConfig
import com.abrigo.itimalia.application.config.EnvironmentConfig
import org.koin.dsl.module

val databaseModule = module {
    single {
        DatabaseConfig(
            get<EnvironmentConfig>().jdbcUrl(),
            get<EnvironmentConfig>().databaseUsername(),
            get<EnvironmentConfig>().databasePassword()
        )
    }
}
