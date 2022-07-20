package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.application.web.accessmanagers.JWTAccessManager
import com.abrigo.itimalia.domain.entities.animal.AnimalRequest
import com.abrigo.itimalia.domain.entities.animal.AnimalSex
import com.abrigo.itimalia.domain.entities.animal.AnimalSize
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.NewAnimalRequest
import com.abrigo.itimalia.domain.entities.animal.Specie
import com.abrigo.itimalia.domain.entities.filter.FilterOptions
import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.domain.services.AnimalService
import com.abrigo.itimalia.domain.services.UserService
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus

class AnimalController(
    private val animalService: AnimalService,
    private val jwtAccessManager: JWTAccessManager,
    private val userService: UserService
) {

    fun addAnimal(context: Context) {
        val newAnimal = context.bodyAsClass<NewAnimalRequest>()
        val email = jwtAccessManager.extractEmail(context)
        val user = userService.findByEmail(email)
        val newAnimalDTO = animalService.add(newAnimal, user.id ?: throw IllegalArgumentException())
        context.json(newAnimalDTO).status(HttpStatus.CREATED_201)
    }

    fun findAnimal(context: Context) {
        val id: Int = context.pathParam("id").toInt()
        val animal = animalService.get(id)
        context.json(animal).status(HttpStatus.OK_200)
    }

    fun updateAnimal(context: Context) {
        val id: Int = context.pathParam("id").toInt()
        val animalToBeUpdated = context.bodyAsClass<AnimalRequest>()
        val updatedAnimal = animalService.update(id, animalToBeUpdated)
        context.json(updatedAnimal).status(HttpStatus.OK_200)
    }

    fun deleteAnimal(context: Context) {
        val id: Int = context.pathParam("id").toInt()
        animalService.delete(id)
        context.status(HttpStatus.NO_CONTENT_204)
    }

    fun adopt(context: Context) {
        val id: Int = context.pathParam("id").toInt()
        val email = jwtAccessManager.extractEmail(context)
        val user = userService.findByEmail(email)
        val adoptedAnimal = animalService.adopt(id, user.id ?: throw IllegalArgumentException())
        context.json(adoptedAnimal).status(HttpStatus.OK_200)
    }

    fun findAllAnimals(context: Context) {
        val status = context.queryParam("status")
        val specie = context.queryParam("specie")
        val sex = context.queryParam("sex")
        val size = context.queryParam("size")
        val name = context.queryParam("name")
        val castrated = context.queryParam("castrated")

        val filterOptions = FilterOptions(
            status = if (status.isNullOrBlank()) null else getEnumFrom<AnimalStatus>(status),
            specie = if (specie.isNullOrBlank()) null else getEnumFrom<Specie>(specie),
            sex = if (sex.isNullOrBlank()) null else getEnumFrom<AnimalSex>(sex),
            size = if (size.isNullOrBlank()) null else getEnumFrom<AnimalSize>(size),
            name = if (name.isNullOrBlank()) null else name,
            castrated = if (castrated.isNullOrBlank()) null else castrated.toBoolean()
        )
        val animals = animalService.getAll(filterOptions)
        context.json(animals).status(HttpStatus.OK_200)
    }

    private inline fun <reified E : Enum<E>> getEnumFrom(string: String): E {
        return try {
            enumValueOf(string.uppercase())
        } catch (exception: IllegalArgumentException) {
            // log this error
            throw ValidationException(mapOf("field" to mutableListOf("Invalid field '$string'. Please update this value with expected values: ${enumValues<E>().map { it.name }}")))
        }
    }
}
