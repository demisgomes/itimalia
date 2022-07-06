package com.abrigo.itimalia.application.web.swagger

import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.User
import com.abrigo.itimalia.domain.entities.user.UserLogin
import com.abrigo.itimalia.domain.entities.user.UserPublicInfo
import com.abrigo.itimalia.domain.exceptions.ErrorResponse
import io.javalin.plugin.openapi.dsl.OpenApiDocumentation
import io.swagger.v3.oas.models.security.SecurityRequirement

object SwaggerUserDocumentation {

    fun getUserDocumentation() = OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .json("200", UserPublicInfo::class.java)

    fun createUserDocumentation() =
        OpenApiDocumentation()
            .body<NewUser>(CONTENT_TYPE)
            .json("200", User::class.java)
            .result("400", ErrorResponse::class.java)

    fun updateUserDocumentation() = OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .body<User>(CONTENT_TYPE)
        .json("200", User::class.java)
        .result("400", ErrorResponse::class.java)
        .result("404", Unit::class.java)

    fun deleteUserDocumentation() = OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .result("204", null)
        .result("403", ErrorResponse::class.java)
        .result("404", Unit::class.java)
        .operation {
            it.addSecurityItem(SecurityRequirement().addList("BearerAuth"))
        }

    fun loginUserDocumentation() = OpenApiDocumentation()
        .body<UserLogin>(CONTENT_TYPE)
        .result("200", User::class.java)
        .result("401", ErrorResponse::class.java)

    fun createAdminDocumentation() = OpenApiDocumentation()
        .body<NewUser>(CONTENT_TYPE)
        .json("200", User::class.java)
        .result("400", ErrorResponse::class.java)
        .operation {
            it.addSecurityItem(SecurityRequirement().addList("BearerAuth"))
        }
}
