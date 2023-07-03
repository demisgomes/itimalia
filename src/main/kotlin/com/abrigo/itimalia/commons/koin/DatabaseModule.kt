package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.application.config.DatabaseConfig
import com.abrigo.itimalia.application.config.EnvironmentConfig
import com.abrigo.itimalia.domain.validation.ConstraintValidator
import com.abrigo.itimalia.domain.validation.Request
import com.abrigo.itimalia.domain.validation.ValidatorRequest
import com.abrigo.itimalia.domain.validation.impl.ValidatorRequestImpl
import com.abrigo.itimalia.resources.validation.hibernate.HibernateValidator
import com.abrigo.itimalia.resources.validation.hibernate.gateways.ConstraintRequestValidator
import org.koin.dsl.module

val databaseModule = module {
    single {
        DatabaseConfig(
            EnvironmentConfig.jdbcUrl(),
            EnvironmentConfig.databaseUsername(),
            EnvironmentConfig.databasePassword()
        )
    }
}
