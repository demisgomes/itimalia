package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.domain.entities.Roles
import com.abrigo.itimalia.domain.jwt.JWTAccessManager
import org.koin.dsl.module.module

val accessManagerModule= module{
    single { JWTAccessManager("role",mutableMapOf("user" to Roles.USER,"admin" to Roles.ADMIN), Roles.ANYONE) }
}