package application.web

import application.config.DatabaseConfig
import application.config.RouteConfig
import com.fasterxml.jackson.databind.ObjectMapper
import commons.koin.*
import domain.jwt.JWTAccessManager
import io.javalin.Javalin
import io.javalin.json.JavalinJackson
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject

class ItimaliaApplication : KoinComponent {
    private val routeConfig: RouteConfig by inject()
    private val jwtAccessManager: JWTAccessManager by inject()
    private val objectMapper:ObjectMapper by inject()

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
            .create()
            .accessManager(jwtAccessManager)
            .start(getHerokuAssignedPort())

        JavalinJackson.configure(objectMapper)
        routeConfig.register(app)
        initDB()
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

private fun initDB(){
    DatabaseConfig.connect( "jdbc:h2:mem:test;MODE=MySQL;", "sa", "")
    DatabaseConfig.createTables()
}