package application.web

import application.config.RouteConfig
import commons.koin.configModule
import commons.koin.controllerModule
import commons.koin.serviceModule
import commons.koin.validationModule
import io.javalin.Javalin
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject

class ItimaliaApplication : KoinComponent {
    private val routeConfig: RouteConfig by inject()

    fun startServer() {
        val app = Javalin.create().start(getHerokuAssignedPort())

        StandAloneContext.startKoin(
            listOf(
                validationModule,
                serviceModule,
                controllerModule,
                configModule
            )
        )
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

