package com.abrigo.itimalia.application.web.swagger

import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.UserDTO
import com.abrigo.itimalia.domain.entities.user.UserLogin
import com.abrigo.itimalia.domain.entities.user.UserSearched
import io.javalin.plugin.openapi.dsl.OpenApiDocumentation

object SwaggerUserDocumentation{

    fun getUserDocumentation()= OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .json("200", UserSearched::class.java)

    fun createUserDocumentation() = OpenApiDocumentation()
        .body<NewUser>(CONTENT_TYPE)
        .json("200", UserDTO::class.java)

    fun updateUserDocumentation() = OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .body<UserDTO>(CONTENT_TYPE)
        .json("200", UserDTO::class.java)

    fun deleteUserDocumentation() = OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .result("204", null)

    fun loginUserDocumentation() = OpenApiDocumentation()
        .body<UserLogin>(CONTENT_TYPE)
        .result("200",UserDTO::class.java)

    fun createAdminDocumentation()= OpenApiDocumentation()
        .body<NewUser>(CONTENT_TYPE)
        .json("200", UserDTO::class.java)
}