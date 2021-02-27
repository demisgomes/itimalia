package com.abrigo.itimalia.application.web.swagger

import com.abrigo.itimalia.domain.entities.animal.Animal
import com.abrigo.itimalia.domain.entities.animal.NewAnimal
import com.abrigo.itimalia.domain.exceptions.ErrorResponse
import io.javalin.plugin.openapi.dsl.OpenApiDocumentation
import io.swagger.v3.oas.models.security.SecurityRequirement

object SwaggerAnimalDocumentation{
    fun createAnimalDocumentation() = OpenApiDocumentation()
        .body<NewAnimal>(CONTENT_TYPE)
        .json("200", Animal::class.java)
        .result("403", ErrorResponse::class.java)
        .operation {
            it.addSecurityItem(SecurityRequirement().addList("BearerAuth"))
        }

    fun updateAnimalDocumentation() = OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .body<Animal>(CONTENT_TYPE)
        .json("200", Animal::class.java)
        .result("403", ErrorResponse::class.java)
        .result("404", Unit::class.java)
        .operation {
            it.addSecurityItem(SecurityRequirement().addList("BearerAuth"))
        }

    fun deleteAnimalDocumentation() = OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .result("204", null)
        .result("403", ErrorResponse::class.java)
        .result("404", Unit::class.java)
        .operation {
            it.addSecurityItem(SecurityRequirement().addList("BearerAuth"))
        }

    fun getAnimalDocumentation()= OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .json("200", Animal::class.java)

    fun getAnimalsDocumentation()= OpenApiDocumentation()
        .queryParam("specie", String::class.java)
        .queryParam("status", String::class.java)
        .queryParam("name", String::class.java)
        .json("200", Animal::class.java)

    fun adoptAnimalDocumentation() = OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .json("200", Animal::class.java)
        .result("403", ErrorResponse::class.java)
        .result("404", Unit::class.java)
        .result("410", Unit::class.java)
        .operation {
            it.addSecurityItem(SecurityRequirement().addList("BearerAuth"))
        }
}