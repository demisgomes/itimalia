package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.domain.jwt.JWTService
import com.abrigo.itimalia.resources.jwt.auth0.gateways.JWTServiceImpl
import org.koin.dsl.module

val JWTModule = module {
    single { JWTServiceImpl(get()) as JWTService }
}
