package com.abrigo.itimalia.application.config

object EnvironmentConfig {

    fun jdbcUrl(): String {
        return getenv("JDBC_URL", "jdbc:h2:mem:test;MODE=MySQL;")
    }

    private fun getenv(env: String, defaultValue: String): String {
        return System.getenv(env) ?: defaultValue
    }

    fun databaseUsername(): String {
        return getenv("DATABASE_USERNAME", "zhprkfrmzwkuzg")
    }

    fun databasePassword(): String {
        return getenv("DATABASE_PASSWORD", "a8d4f5c48841b91202a095682df15124f979e9a20599e036a31c2d06636669ca")
    }
}