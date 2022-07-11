package com.abrigo.itimalia.application.web.accessmanagers.entities

import io.javalin.core.security.RouteRole

enum class RouteRole : RouteRole {
    ANYONE,
    USER,
    ADMIN
}
