package com.abrigo.itimalia.application.config

object EnvironmentConfig {

    fun jdbcUrl(): String {
        return getenv("JDBC_URL", "jdbc:h2:mem:test;MODE=MySQL;")
    }

    private fun getenv(env: String, defaultValue: String): String {
        return System.getenv(env) ?: defaultValue
    }

    fun databaseUsername(): String {
        return getenv("DATABASE_USERNAME", "sa")
    }

    fun databasePassword(): String {
        return getenv("DATABASE_PASSWORD", "")
    }
}