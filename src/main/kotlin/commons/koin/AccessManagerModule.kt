package commons.koin

import application.config.RouteConfig
import domain.entities.Roles
import domain.jwt.JWTAccessManager
import org.koin.dsl.module.module

val accessManagerModule= module{
    single { JWTAccessManager("role",mutableMapOf("user" to Roles.USER,"admin" to Roles.ADMIN), Roles.ANYONE) }
}