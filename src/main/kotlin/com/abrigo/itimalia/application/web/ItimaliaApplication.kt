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
import com.abrigo.itimalia.commons.koin.passwordModule
import com.abrigo.itimalia.commons.koin.repositoryModule
import com.abrigo.itimalia.commons.koin.serviceModule
import com.abrigo.itimalia.commons.koin.validationModule
import com.abrigo.itimalia.domain.exceptions.ApiException
import com.fasterxml.jackson.databind.ObjectMapper
import io.javalin.Javalin
import io.javalin.http.BadRequestResponse
import io.javalin.plugin.json.JavalinJackson
import org.apache.logging.log4j.LogManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin

class ItimaliaApplication : KoinComponent {
    private val routeConfig: RouteConfig by inject()
    private val jwtAccessManager: JWTAccessManager by inject()
    private val objectMapper: ObjectMapper by inject()
    private val errorHandler: ErrorHandler by inject()

    companion object {
        private val logger = LogManager.getLogger()
    }

    fun startServer() {
        startKoin {
            modules(
                serviceModule,
                controllerModule,
                configModule,
                JWTModule,
                accessManagerModule,
                repositoryModule,
                imageModule,
                passwordModule,
                validationModule
            )
        }

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
    }
}

fun main() {
    ItimaliaApplication().startServer()
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
