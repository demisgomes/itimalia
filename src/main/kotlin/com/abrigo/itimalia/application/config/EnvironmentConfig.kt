package com.abrigo.itimalia.application.config

import io.github.cdimascio.dotenv.Dotenv

class EnvironmentConfig(private val dotenv: Dotenv) {

    fun jdbcUrl(): String {
        return getenv("JDBC_URL", "jdbc:h2:mem:test;MODE=MySQL;")
    }

    private fun getenv(env: String, defaultValue: String): String {
        return dotenv[env] ?: defaultValue
    }

    fun databaseUsername(): String {
        return getenv("DATABASE_USERNAME", "sa")
    }

    fun databasePassword(): String {
        return getenv("DATABASE_PASSWORD", "")
    }

    fun issuer(): String {
        return getenv("ISSUER", "")
    }

    fun jwtSecret(): String {
        return getenv("JWT_SECRET", "")
    }

    fun jwtExpirationInMinutes(): String {
        return getenv("JWT_EXPIRATION_MINUTES", "5")
    }

    fun maxFileSize(): String {
        return getenv("MAX_FILE_SIZE", "1048576")
    }

    fun maxNumberOfImages(): String {
        return getenv("MAX_NUMBER_OF_IMAGES", "8")
    }

    fun hashSalt(): String {
        return getenv("HASH_SALT", "12")
    }

    fun maxPageLimit(): String {
        return getenv("MAX_PAGE_LIMIT", "50")
    }
}
