package com.abrigo.itimalia.resources.jwt.auth0.gateways

import com.abrigo.itimalia.application.config.EnvironmentConfig
import com.abrigo.itimalia.domain.entities.user.UserRole
import com.abrigo.itimalia.domain.exceptions.InvalidTokenException
import com.abrigo.itimalia.domain.jwt.JWTService
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.TokenExpiredException
import org.slf4j.LoggerFactory
import java.util.Calendar
import java.util.Date

class JWTServiceImpl(private val environmentConfig: EnvironmentConfig) : JWTService {

    companion object {
        private val logger = LoggerFactory.getLogger(JWTServiceImpl::class.java)
        const val EMAIL_CLAIM = "email"
        const val ROLE_CLAIM = "role"
    }

    override fun sign(email: String, role: UserRole): String {
        val algorithm = Algorithm.HMAC256(environmentConfig.jwtSecret())

        return JWT
            .create()
            .withIssuer(environmentConfig.issuer())
            .withClaim(EMAIL_CLAIM, email)
            .withClaim(ROLE_CLAIM, role.toString())
            .withExpiresAt(convertToDate(Integer.parseInt(environmentConfig.jwtExpirationInMinutes())))
            .sign(algorithm)
    }

    override fun decode(token: String): Map<String, String> {
        try {
            val claims = mutableMapOf<String, String>()

            val algorithm = Algorithm.HMAC256(environmentConfig.jwtSecret())
            val verifier = JWT.require(algorithm)
                .withIssuer(environmentConfig.issuer())
                .build()

            val decodedJWT = verifier.verify(token)

            decodedJWT.claims.forEach { claim ->
                if (claim.key == EMAIL_CLAIM || claim.key == ROLE_CLAIM) {
                    claims[claim.key] = claim.value.asString()
                }
            }

            return claims
        } catch (exception: JWTDecodeException) {
            logger.warn("The token cannot be decoded", exception)
            throw InvalidTokenException()
        } catch (exception: TokenExpiredException) {
            logger.warn("The token expired", exception)
            throw InvalidTokenException()
        }
    }

    private fun convertToDate(minutes: Int): Date {
        val calendar = Calendar.getInstance()
        return Date(calendar.timeInMillis + minutes * 60000)
    }
}
