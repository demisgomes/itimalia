package com.abrigo.itimalia.application.web

import com.abrigo.itimalia.application.config.DatabaseConfig
import com.abrigo.itimalia.application.config.RouteConfig
import com.abrigo.itimalia.application.web.handlers.ErrorHandler
import com.abrigo.itimalia.application.web.swagger.SwaggerConfig
import com.abrigo.itimalia.commons.koin.*
import com.abrigo.itimalia.domain.exceptions.ApiException
import com.abrigo.itimalia.domain.jwt.JWTAccessManager
import com.fasterxml.jackson.databind.ObjectMapper
import io.javalin.Javalin
import io.javalin.http.BadRequestResponse
import io.javalin.plugin.json.JavalinJackson
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject

class ItimaliaApplication : KoinComponent {
    private val routeConfig: RouteConfig by inject()
    private val jwtAccessManager: JWTAccessManager by inject()
    private val objectMapper:ObjectMapper by inject()
    private val errorHandler:ErrorHandler by inject()

    fun startServer() {

        StandAloneContext.startKoin(
            listOf(
                validationModule,
                serviceModule,
                controllerModule,
                configModule,
                JWTModule,
                accessManagerModule,
                repositoryModule
            )
        )

        val app = Javalin
            .create { config ->
                SwaggerConfig.registerPlugin(config)
                config.accessManager(jwtAccessManager)
            }
            .exception(ApiException::class.java){ exception, ctx ->
                errorHandler.handleApiException(exception, ctx)
            }
            .exception(Exception::class.java){ exception, ctx ->
                errorHandler.handleGenericException(exception, ctx)
            }
            .exception(BadRequestResponse::class.java){ exception, ctx ->
                errorHandler.handleBadRequestResponse(exception, ctx)
            }
            .exception(IllegalArgumentException::class.java){ exception, ctx ->
                errorHandler.handleIllegalArgumentException(exception, ctx)
            }
            .start(getHerokuAssignedPort())

        JavalinJackson.configure(objectMapper)
        routeConfig.register(app)
        initDB()
    }
}

fun main() {
    try{
        ItimaliaApplication().startServer()
    }
    catch (exception:Exception){
        exception.printStackTrace()
    }
}

private fun getHerokuAssignedPort(): Int {
    val processBuilder = ProcessBuilder()
    return if (processBuilder.environment()["PORT"] != null) {
        Integer.parseInt(processBuilder.environment()["PORT"])
    } else 7000
}

private fun initDB(){
    DatabaseConfig.connect( "jdbc:h2:mem:test;MODE=MySQL;", "sa", "")
    DatabaseConfig.createTables()
}