package com.abrigo.itimalia.application.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

class DatabaseConfig(
    private val jdbcUrl: String,
    private val username: String,
    private val password: String) {

    fun connect() {
        val hikariConfig = HikariConfig()

        hikariConfig.jdbcUrl = jdbcUrl
        hikariConfig.username = username
        hikariConfig.password = password

        val hikariDataSource = HikariDataSource(hikariConfig)

        Database.connect(hikariDataSource)
    }

    fun createTables() {
        val flyway = Flyway.configure().dataSource(
            jdbcUrl,
            username,
            password
        ).locations("db/migration-postgres").load()

        flyway.migrate()
    }
}
