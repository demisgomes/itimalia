package com.abrigo.itimalia.application.config

object EnvironmentConfig {

    fun jdbcUrl(): String {
        return getProperty("JDBC_URL", "jdbc:h2:mem:test;MODE=MySQL")
    }

    private fun getProperty(property: String, defaultValue: String): String {
        return if (System.getProperty(property) != null) System.getProperty(property)
        else defaultValue
    }

    fun databaseUsername() : String{
        return getProperty("DATABASE_USERNAME", "sa")
    }

    fun databasePassword() : String{
        return getProperty("DATABASE_PASSWORD", "")
    }
}