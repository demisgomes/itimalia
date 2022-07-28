package com.abrigo.itimalia.application.web.swagger

import com.abrigo.itimalia.domain.entities.animal.Animal
import com.abrigo.itimalia.domain.entities.animal.AnimalSex
import com.abrigo.itimalia.domain.entities.animal.AnimalSize
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.NewAnimal
import com.abrigo.itimalia.domain.entities.animal.Specie
import com.abrigo.itimalia.domain.entities.image.Image
import com.abrigo.itimalia.domain.entities.paging.Direction
import com.abrigo.itimalia.domain.entities.paging.OrderBy
import com.abrigo.itimalia.domain.entities.paging.Page
import com.abrigo.itimalia.domain.exceptions.ErrorResponse
import io.javalin.plugin.openapi.dsl.OpenApiDocumentation
import io.swagger.v3.oas.models.security.SecurityRequirement

object SwaggerAnimalDocumentation {
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

    fun getAnimalDocumentation() = OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .json("200", Animal::class.java)

    fun getAnimalsDocumentation() = OpenApiDocumentation()
        .queryParam("specie", Specie::class.java)
        .queryParam("status", AnimalStatus::class.java)
        .queryParam("name", String::class.java)
        .queryParam("castrated", Boolean::class.java)
        .queryParam("size", AnimalSize::class.java)
        .queryParam("sex", AnimalSex::class.java)
        .queryParam("page", Int::class.java)
        .queryParam("limit", Int::class.java)
        .queryParam("orderBy", OrderBy::class.java)
        .queryParam("direction", Direction::class.java)
        .json("200", Page::class.java)

    fun adoptAnimalDocumentation() = OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .json("200", Animal::class.java)
        .result("403", ErrorResponse::class.java)
        .result("404", Unit::class.java)
        .result("410", Unit::class.java)
        .operation {
            it.addSecurityItem(SecurityRequirement().addList("BearerAuth"))
        }

    fun addAnimalImagesDocumentation() = OpenApiDocumentation()
        .pathParam("id", String::class.java)
        .jsonArray<Image>("200")
        .result("401", ErrorResponse::class.java)
        .result("403", ErrorResponse::class.java)
        .result("404", ErrorResponse::class.java)
        .operation {
            it.addSecurityItem(SecurityRequirement().addList("BearerAuth"))
        }
}
