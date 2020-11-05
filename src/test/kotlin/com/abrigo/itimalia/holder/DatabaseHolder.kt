package com.abrigo.itimalia.holder

import com.abrigo.itimalia.application.config.DatabaseConfig
import org.flywaydb.core.Flyway
import org.h2.tools.Server


object DatabaseHolder {
    private const val JDBC_URL = "jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
    private const val DATABASE_USERNAME = "sa"
    private const val DATABASE_PASSWORD = ""

    private val flyway = Flyway.configure().dataSource(
        JDBC_URL,
        DATABASE_USERNAME,
        DATABASE_PASSWORD
    ).load()

    init {
        DatabaseConfig.connect(
            JDBC_URL,
            DATABASE_USERNAME,
            DATABASE_PASSWORD
        )
        flyway.migrate()
    }

    fun start() {
        Server.createPgServer().start()
    }

    fun tearDown() {
        flyway.clean()
        flyway.migrate()
    }

    fun stop() {
        Server.createPgServer().stop()
    }

}