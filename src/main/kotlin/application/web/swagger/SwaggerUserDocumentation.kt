package application.web.swagger

import domain.entities.user.NewUser
import domain.entities.user.UserDTO
import domain.entities.user.UserLogin
import domain.entities.user.UserSearched
import domain.exceptions.UserNotFoundException
import io.javalin.plugin.openapi.dsl.OpenApiDocumentation

object SwaggerUserDocumentation{

    fun getUserDocumentation()= OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .json("200", UserSearched::class.java)

    fun createUserDocumentation() = OpenApiDocumentation()
        .body<NewUser>("application/json")
        .json("200", UserDTO::class.java)

    fun updateUserDocumentation() = OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .body<UserDTO>("application/json")
        .json("200", UserDTO::class.java)

    fun deleteUserDocumentation() = OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .result("204", null)

    fun loginUserDocumentation() = OpenApiDocumentation()
        .body<UserLogin>("application/json")
        .result("200",UserDTO::class.java)

    fun createAdminDocumentation()= OpenApiDocumentation()
        .body<NewUser>("application/json")
        .json("200", UserDTO::class.java)
}