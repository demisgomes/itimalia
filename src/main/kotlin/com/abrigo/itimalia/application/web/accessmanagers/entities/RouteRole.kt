package com.abrigo.itimalia.application.web.accessmanagers.entities

import io.javalin.core.security.Role

enum class RouteRole : Role{
    ANYONE,
    USER,
    ADMIN
}