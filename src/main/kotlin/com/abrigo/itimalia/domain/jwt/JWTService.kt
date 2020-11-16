package com.abrigo.itimalia.domain.jwt

import com.abrigo.itimalia.domain.entities.user.UserRole

interface JWTService {
    fun sign(email: String, role: UserRole): String
    fun decode(token: String) : Map<String, String>
}