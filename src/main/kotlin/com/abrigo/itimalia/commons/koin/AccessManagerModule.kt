package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.application.web.accessmanagers.JWTAccessManager
import com.abrigo.itimalia.domain.entities.user.UserRole
import org.koin.dsl.module

val accessManagerModule = module {
    single {
        JWTAccessManager(
            mutableMapOf("user" to UserRole.USER, "admin" to UserRole.ADMIN),
            UserRole.ANYONE,
            get()
        )
    }
}
