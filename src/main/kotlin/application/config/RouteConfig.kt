package application.config

import application.web.controllers.UserController
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder

class RouteConfig(private val userController: UserController){
    fun register(app: Javalin) {

        app.get("/") { ctx -> ctx.json(mapOf("message" to " starting server")) }

        app.routes {
            ApiBuilder.path("users") {
                ApiBuilder.post(userController::addUser)
                ApiBuilder.path(":id"){
                    ApiBuilder.get(userController::findUser)
                    ApiBuilder.put(userController::updateUser)
                    ApiBuilder.delete(userController::deleteUser)
                }
            }

        }
    }
}