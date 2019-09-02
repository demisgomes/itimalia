package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.domain.jwt.JWTUtils
import org.koin.dsl.module.module

val JWTModule = module{
    single{ JWTUtils() }
}