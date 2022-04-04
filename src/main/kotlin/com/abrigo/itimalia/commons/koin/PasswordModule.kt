package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.domain.jwt.JWTService
import com.abrigo.itimalia.domain.services.PasswordService
import com.abrigo.itimalia.resources.jwt.auth0.gateways.JWTServiceImpl
import com.abrigo.itimalia.resources.password.bcrypt.BCryptPasswordService
import org.koin.dsl.module.module

val PasswordModule = module{
    single{ BCryptPasswordService() as PasswordService }
}