package com.abrigo.itimalia.application.web.swagger

import com.abrigo.itimalia.domain.entities.animal.Animal
import com.abrigo.itimalia.domain.entities.animal.NewAnimal
import io.javalin.plugin.openapi.dsl.OpenApiDocumentation

object SwaggerAnimalDocumentation{
    fun createAnimalDocumentation() = OpenApiDocumentation()
        .body<NewAnimal>(CONTENT_TYPE)
        .json("200", Animal::class.java)

    fun updateAnimalDocumentation() = OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .body<Animal>(CONTENT_TYPE)
        .json("200", Animal::class.java)

    fun deleteAnimalDocumentation() = OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .result("204", null)

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
}