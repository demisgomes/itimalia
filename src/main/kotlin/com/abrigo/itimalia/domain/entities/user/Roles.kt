package com.abrigo.itimalia.domain.entities.user

import io.javalin.core.security.Role

enum class Roles: Role {
    ANYONE,
    USER,
    ADMIN
}