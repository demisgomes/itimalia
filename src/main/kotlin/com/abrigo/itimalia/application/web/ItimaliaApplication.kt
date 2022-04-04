package com.abrigo.itimalia.application.web

import com.abrigo.itimalia.application.config.DatabaseConfig
import com.abrigo.itimalia.application.config.EnvironmentConfig
import com.abrigo.itimalia.application.config.RouteConfig
import com.abrigo.itimalia.application.web.accessmanagers.JWTAccessManager
import com.abrigo.itimalia.application.web.handlers.ErrorHandler
import com.abrigo.itimalia.application.web.swagger.SwaggerConfig
import com.abrigo.itimalia.commons.koin.JWTModule
import com.abrigo.itimalia.commons.koin.accessManagerModule
import com.abrigo.itimalia.commons.koin.configModule
import com.abrigo.itimalia.commons.koin.controllerModule
import com.abrigo.itimalia.commons.koin.imageModule
import com.abrigo.itimalia.commons.koin.repositoryModule
import com.abrigo.itimalia.commons.koin.serviceModule
import com.abrigo.itimalia.commons.koin.passwordModule
import com.abrigo.itimalia.domain.exceptions.ApiException
import com.abrigo.itimalia.domain.services.PasswordService
import com.abrigo.itimalia.resources.storage.exposed.entities.UserEntity
import com.abrigo.itimalia.resources.storage.exposed.entities.UserMap
import com.fasterxml.jackson.databind.ObjectMapper
import io.javalin.Javalin
import io.javalin.http.BadRequestResponse
import io.javalin.plugin.json.JavalinJackson
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject

class ItimaliaApplication : KoinComponent {
    private val routeConfig: RouteConfig by inject()
    private val jwtAccessManager: JWTAccessManager by inject()
    private val objectMapper: ObjectMapper by inject()
    private val errorHandler: ErrorHandler by inject()
    private val passwordService: PasswordService by inject()

    companion object {
        private val logger = LogManager.getLogger()
    }

    fun startServer() {

        StandAloneContext.startKoin(
            listOf(
                serviceModule,
                controllerModule,
                configModule,
                JWTModule,
                accessManagerModule,
                repositoryModule,
                imageModule,
                passwordModule
            )
        )

        val app = Javalin
            .create { config ->
                SwaggerConfig.registerPlugin(config)
                config.accessManager(jwtAccessManager)
            }
            .exception(ApiException::class.java) { exception, ctx ->
                errorHandler.handleApiException(exception, ctx)
            }
            .exception(Exception::class.java) { exception, ctx ->
                errorHandler.handleGenericException(exception, ctx)
            }
            .exception(BadRequestResponse::class.java) { exception, ctx ->
                errorHandler.handleBadRequestResponse(exception, ctx)
            }
            .exception(IllegalArgumentException::class.java) { exception, ctx ->
                errorHandler.handleIllegalArgumentException(exception, ctx)
            }
            .start(getHerokuAssignedPort())

        JavalinJackson.configure(objectMapper)
        routeConfig.register(app)
        logger.info("Connecting to DB")
        initDB()
        logger.info("Connected with DB successfully")

        transaction {
            UserEntity.all().forEach { userEntity ->
                transaction {
                    (UserMap).update({ UserMap.id eq userEntity.id }) {
                        it[password] = passwordService.encode(userEntity.password)
                    }
                }

            }
        }


    }
}

fun main() {
    try {
        ItimaliaApplication().startServer()
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
}

private fun getHerokuAssignedPort(): Int {
    val processBuilder = ProcessBuilder()
    return if (processBuilder.environment()["PORT"] != null) {
        Integer.parseInt(processBuilder.environment()["PORT"])
    } else 7000
}

private fun initDB() {
    DatabaseConfig.connect(
        EnvironmentConfig.jdbcUrl(),
        EnvironmentConfig.databaseUsername(),
        EnvironmentConfig.databasePassword()
    )
    DatabaseConfig.createTables()
}
