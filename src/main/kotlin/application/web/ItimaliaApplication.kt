package application.web

import application.config.RouteConfig
import commons.koin.*
import domain.entities.Roles
import domain.jwt.JWTAccessManager
import io.javalin.Javalin
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject

class ItimaliaApplication : KoinComponent {
    private val routeConfig: RouteConfig by inject()
    private val jwtAccessManager: JWTAccessManager by inject()

    fun startServer() {

        StandAloneContext.startKoin(
            listOf(
                validationModule,
                serviceModule,
                controllerModule,
                configModule,
                JWTModule,
                accessManagerModule
            )
        )

        val app = Javalin
            .create()
            .accessManager(jwtAccessManager)
            .start(getHerokuAssignedPort())

        
        routeConfig.register(app)
    }
}

fun main(args: Array<String>) {
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

