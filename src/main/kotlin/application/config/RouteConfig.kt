package application.config

import application.web.controllers.ItimaliaController
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder

class RouteConfig(private val itimaliaController: ItimaliaController){
    fun register(app: Javalin) {

        app.get("/") { ctx -> ctx.json(mapOf("message" to " starting server")) }

        app.routes {
            ApiBuilder.path("users") {
                ApiBuilder.post(itimaliaController::addUser)
                ApiBuilder.path(":id"){
                    ApiBuilder.get(itimaliaController::findUser)
                }
            }

        }
    }
}