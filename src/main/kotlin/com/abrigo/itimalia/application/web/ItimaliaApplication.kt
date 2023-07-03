package com.abrigo.itimalia.application.web

import com.abrigo.itimalia.application.config.DatabaseConfig
import com.abrigo.itimalia.application.web.accessmanagers.JWTAccessManager
import com.abrigo.itimalia.application.web.accessmanagers.entities.RouteRole
import com.abrigo.itimalia.application.web.controllers.AdminController
import com.abrigo.itimalia.application.web.controllers.AnimalController
import com.abrigo.itimalia.application.web.controllers.AnimalImageController
import com.abrigo.itimalia.application.web.controllers.UserController
import com.abrigo.itimalia.application.web.handlers.ErrorHandler
import com.abrigo.itimalia.application.web.swagger.SwaggerAnimalDocumentation
import com.abrigo.itimalia.application.web.swagger.SwaggerConfig
import com.abrigo.itimalia.application.web.swagger.SwaggerUserDocumentation
import com.abrigo.itimalia.commons.koin.*
import com.abrigo.itimalia.domain.exceptions.ApiException
import com.fasterxml.jackson.databind.ObjectMapper
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.http.BadRequestResponse
import io.javalin.plugin.json.JavalinJackson
import io.javalin.plugin.openapi.dsl.documented
import org.apache.logging.log4j.LogManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin

private const val PORT = 7000

class ItimaliaApplication : KoinComponent {
    private val jwtAccessManager: JWTAccessManager by inject()
    private val objectMapper: ObjectMapper by inject()
    private val errorHandler: ErrorHandler by inject()
    private val databaseConfig: DatabaseConfig by inject()
    private val userController: UserController by inject()
    private val adminController: AdminController by inject()
    private val animalController: AnimalController by inject()
    private val animalImageController: AnimalImageController by inject()

    companion object {
        private val logger = LogManager.getLogger()
    }

    val app: Javalin = Javalin
        .create { config ->
            SwaggerConfig.registerPlugin(config)
            config.accessManager(jwtAccessManager)
            config.jsonMapper(JavalinJackson(objectMapper))
        }
        .routes {
            ApiBuilder.path("users") {
                ApiBuilder.post(documented(SwaggerUserDocumentation.createUserDocumentation(), userController::addUser), RouteRole.ANYONE)
                ApiBuilder.path("{id}") {
                    ApiBuilder.get(documented(SwaggerUserDocumentation.getUserDocumentation(), userController::findUser), RouteRole.ANYONE)
                    ApiBuilder.put(documented(SwaggerUserDocumentation.updateUserDocumentation(), userController::updateUser), RouteRole.USER, RouteRole.ADMIN)
                    ApiBuilder.delete(documented(SwaggerUserDocumentation.deleteUserDocumentation(), userController::deleteUser), RouteRole.USER, RouteRole.ADMIN)
                }
            }
            ApiBuilder.path("login") {
                ApiBuilder.post(documented(SwaggerUserDocumentation.loginUserDocumentation(), userController::loginUser), RouteRole.ANYONE)
            }

            ApiBuilder.path("admins") {
                ApiBuilder.post(documented(SwaggerUserDocumentation.createUserDocumentation(), adminController::addAdminUser), RouteRole.ADMIN)
            }
            ApiBuilder.path("animals") {
                ApiBuilder.path("{id}") {
                    ApiBuilder.get(documented(SwaggerAnimalDocumentation.getAnimalDocumentation(), animalController::findAnimal), RouteRole.ANYONE)
                    ApiBuilder.put(documented(SwaggerAnimalDocumentation.updateAnimalDocumentation(), animalController::updateAnimal), RouteRole.ADMIN)
                    ApiBuilder.delete(documented(SwaggerAnimalDocumentation.deleteAnimalDocumentation(), animalController::deleteAnimal), RouteRole.ADMIN)
                    ApiBuilder.path("images") {
                        ApiBuilder.post(documented(SwaggerAnimalDocumentation.addAnimalImagesDocumentation(), animalImageController::addImage), RouteRole.ADMIN)
                    }
                }
                ApiBuilder.post(documented(SwaggerAnimalDocumentation.createAnimalDocumentation(), animalController::addAnimal), RouteRole.ADMIN)
                ApiBuilder.get(documented(SwaggerAnimalDocumentation.getAnimalsDocumentation(), animalController::findAllAnimals), RouteRole.ANYONE)
            }
            ApiBuilder.path("animals/adopt/{id}") {
                ApiBuilder.post(documented(SwaggerAnimalDocumentation.adoptAnimalDocumentation(), animalController::adopt), RouteRole.USER)
            }
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

    fun startServer() {
        startKoin()
        app.start(PORT)

        initDB()
    }

    private fun startKoin() {
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
                validationModule,
                databaseModule,
                envModule
            )
        }
    }

    private fun initDB() {
        logger.info("Connecting to DB")
        databaseConfig.connect()
        databaseConfig.createTables()
        logger.info("Connected with DB successfully")
    }
}

fun main() {
    ItimaliaApplication().startServer()
}
