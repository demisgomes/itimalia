package com.abrigo.itimalia.factories

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Calendar
import java.util.Date

object TokenFactory {

    private const val jwtSecret = "mysecret"
    private const val issuer = "Itimalia-test"
    private const val jwtExpirationInMinutes = "5"

    fun build(email: String, role: String): String {
        val algorithm = Algorithm.HMAC256(jwtSecret)

        return JWT
            .create()
            .withIssuer(issuer)
            .withClaim("email", email)
            .withClaim("role", role)
            .withExpiresAt(convertToDate(Integer.parseInt(jwtExpirationInMinutes))).sign(algorithm)
    }

    fun buildInvalid() = "axyegebcaclacoamcoamcom"

    private fun convertToDate(minutes: Int): Date {
        val calendar = Calendar.getInstance()
        return Date(calendar.timeInMillis + minutes * 60000)
    }

    fun buildExpired(): String {
        val algorithm = Algorithm.HMAC256(jwtSecret)

        return JWT
            .create()
            .withIssuer(issuer)
            .withClaim("email", "example")
            .withClaim("role", "user")
            .withExpiresAt(convertToDate(-1)).sign(algorithm)
    }
}
