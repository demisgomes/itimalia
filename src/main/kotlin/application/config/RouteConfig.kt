package application.config

import application.web.controllers.AdminController
import application.web.controllers.AnimalController
import application.web.controllers.UserController
import application.web.swagger.SwaggerAnimalDocumentation.adoptAnimalDocumentation
import application.web.swagger.SwaggerAnimalDocumentation.createAnimalDocumentation
import application.web.swagger.SwaggerAnimalDocumentation.deleteAnimalDocumentation
import application.web.swagger.SwaggerAnimalDocumentation.getAnimalDocumentation
import application.web.swagger.SwaggerAnimalDocumentation.getAnimalsDocumentation
import application.web.swagger.SwaggerAnimalDocumentation.updateAnimalDocumentation
import application.web.swagger.SwaggerUserDocumentation.createUserDocumentation
import application.web.swagger.SwaggerUserDocumentation.deleteUserDocumentation
import application.web.swagger.SwaggerUserDocumentation.getUserDocumentation
import application.web.swagger.SwaggerUserDocumentation.loginUserDocumentation
import application.web.swagger.SwaggerUserDocumentation.updateUserDocumentation
import domain.entities.Roles
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.core.security.SecurityUtil.roles
import io.javalin.plugin.openapi.dsl.documented


class RouteConfig(private val userController: UserController, private val adminController: AdminController, private val animalController: AnimalController){
    fun register(app: Javalin) {

        app.routes {
            ApiBuilder.path("users") {
                ApiBuilder.post(documented(createUserDocumentation(), userController::addUser), roles(Roles.ANYONE))
                ApiBuilder.path(":id"){
                    ApiBuilder.get(documented(getUserDocumentation(),userController::findUser),roles(Roles.ANYONE))
                    ApiBuilder.put(documented(updateUserDocumentation(),userController::updateUser),roles(Roles.USER, Roles.ADMIN))
                    ApiBuilder.delete(documented(deleteUserDocumentation(), userController::deleteUser), roles(Roles.USER, Roles.ADMIN))
                }
            }
            ApiBuilder.path("login"){
                ApiBuilder.post(documented(loginUserDocumentation(),userController::loginUser), roles(Roles.ANYONE))
            }

            ApiBuilder.path("admins"){
                ApiBuilder.post(documented(createUserDocumentation(),adminController::addAdminUser), roles(Roles.ADMIN))
            }
            ApiBuilder.path("animals"){
                ApiBuilder.path(":id"){
                    ApiBuilder.get(documented(getAnimalDocumentation(),animalController::findAnimal), roles(Roles.ANYONE))
                    ApiBuilder.put(documented(updateAnimalDocumentation(),animalController::updateAnimal), roles(Roles.ADMIN))
                    ApiBuilder.delete(documented(deleteAnimalDocumentation(),animalController::deleteAnimal), roles(Roles.ADMIN))
                }
                ApiBuilder.post(documented(createAnimalDocumentation(),animalController::addAnimal), roles(Roles.ADMIN))
                ApiBuilder.get(documented(getAnimalsDocumentation(),animalController::findAllAnimals), roles(Roles.ANYONE))

            }
            ApiBuilder.path("animals/adopt/:id"){
                ApiBuilder.post(documented(adoptAnimalDocumentation(),animalController::adopt), roles(Roles.USER))
            }

        }
    }
}