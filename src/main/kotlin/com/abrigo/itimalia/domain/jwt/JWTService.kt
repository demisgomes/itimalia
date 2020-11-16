package com.abrigo.itimalia.domain.jwt

import com.abrigo.itimalia.domain.entities.user.Roles

interface JWTService {
    fun sign(email: String, role: Roles): String
    fun decode(token: String) : Map<String, String>
}