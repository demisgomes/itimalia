package application.web.swagger

import domain.entities.AnimalDTO
import domain.entities.NewAnimal
import io.javalin.plugin.openapi.dsl.OpenApiDocumentation

object SwaggerAnimalDocumentation{
    fun createAnimalDocumentation() = OpenApiDocumentation()
        .body<NewAnimal>("application/json")
        .json("200", AnimalDTO::class.java)

    fun updateAnimalDocumentation() = OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .body<AnimalDTO>("application/json")
        .json("200", AnimalDTO::class.java)

    fun deleteAnimalDocumentation() = OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .result("204", null)

    fun getAnimalDocumentation()= OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .json("200", AnimalDTO::class.java)

    fun getAnimalsDocumentation()= OpenApiDocumentation()
        .queryParam("specie", String::class.java)
        .queryParam("status", String::class.java)
        .queryParam("name", String::class.java)
        .json("200", AnimalDTO::class.java)

    fun adoptAnimalDocumentation() = OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .json("200", AnimalDTO::class.java)
}