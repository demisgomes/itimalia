package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.application.web.accessmanagers.JWTAccessManager
import com.abrigo.itimalia.domain.entities.user.Roles
import org.koin.dsl.module.module

val accessManagerModule = module {
    single {
        JWTAccessManager(
            mutableMapOf("user" to Roles.USER, "admin" to Roles.ADMIN),
            Roles.ANYONE,
            get()
        )
    }
}