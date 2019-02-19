package application.config

import application.web.controllers.AdminController
import application.web.controllers.AnimalController
import application.web.controllers.UserController
import domain.entities.Roles
import domain.jwt.JWTAccessManager
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.security.SecurityUtil.roles
import javax.management.relation.Role


class RouteConfig(private val userController: UserController, private val adminController: AdminController, private val animalController: AnimalController){
    fun register(app: Javalin) {


        app.get("/") { ctx -> ctx.json(mapOf("message" to " starting server")) }

        app.routes {
            ApiBuilder.path("users") {
                ApiBuilder.post(userController::addUser, roles(Roles.ANYONE))
                ApiBuilder.path(":id"){
                    ApiBuilder.get(userController::findUser,roles(Roles.ANYONE))
                    ApiBuilder.put(userController::updateUser,roles(Roles.USER, Roles.ADMIN))
                    ApiBuilder.delete(userController::deleteUser, roles(Roles.USER, Roles.ADMIN))

                }
            }
            ApiBuilder.path("login"){
                ApiBuilder.post(userController::loginUser, roles(Roles.ANYONE))
            }

            ApiBuilder.path("admins"){
                ApiBuilder.post(adminController::addAdminUser, roles(Roles.ANYONE))
            }
            ApiBuilder.path("animals"){
                ApiBuilder.path(":id"){
                    ApiBuilder.get(animalController::findAnimal, roles(Roles.ANYONE))
                    ApiBuilder.put(animalController::updateAnimal, roles(Roles.ADMIN))
                    ApiBuilder.delete(animalController::deleteAnimal, roles(Roles.ADMIN))
                }
                ApiBuilder.post(animalController::addAnimal, roles(Roles.ADMIN))
            }
            ApiBuilder.path("animals/adopt/:id"){
                ApiBuilder.post(animalController::adopt, roles(Roles.USER))
            }

        }
    }
}