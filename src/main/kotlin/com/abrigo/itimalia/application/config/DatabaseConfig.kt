package com.abrigo.itimalia.application.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

object DatabaseConfig {

    fun connect(jdbcUrl: String, username: String, password: String) {
        val hikariConfig = HikariConfig()

        hikariConfig.jdbcUrl = jdbcUrl
        hikariConfig.username = username
        hikariConfig.password = password

        val hikariDataSource = HikariDataSource(hikariConfig)

        Database.connect(hikariDataSource)
    }

    fun createTables() {
        val flyway = Flyway.configure().dataSource(
            EnvironmentConfig.jdbcUrl(),
            EnvironmentConfig.databaseUsername(),
            EnvironmentConfig.databasePassword()
        ).locations("db/migration-postgres").load()

        flyway.migrate()
    }
}
