package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.application.web.accessmanagers.JWTAccessManager
import com.abrigo.itimalia.domain.entities.animal.AnimalRequest
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.NewAnimalRequest
import com.abrigo.itimalia.domain.entities.animal.Specie
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
        val newAnimal = context.body<NewAnimalRequest>()
        val email = jwtAccessManager.extractEmail(context)
        val user = userService.findByEmail(email)
        val newAnimalDTO = animalService.add(newAnimal, user.id ?: throw IllegalArgumentException())
        context.json(newAnimalDTO).status(HttpStatus.CREATED_201)
    }

    fun findAnimal(context: Context) {
        val id: Int = context.pathParam<Int>("id").get()
        val animal = animalService.get(id)
        context.json(animal).status(HttpStatus.OK_200)
    }

    fun updateAnimal(context: Context) {
        val id: Int = context.pathParam<Int>("id").get()
        val animalToBeUpdated = context.body<AnimalRequest>()
        val updatedAnimal = animalService.update(id, animalToBeUpdated)
        context.json(updatedAnimal).status(HttpStatus.OK_200)
    }

    fun deleteAnimal(context: Context) {
        val id: Int = context.pathParam<Int>("id").get()
        animalService.delete(id)
        context.status(HttpStatus.NO_CONTENT_204)
    }

    fun adopt(context: Context) {
        val id: Int = context.pathParam<Int>("id").get()
        val email = jwtAccessManager.extractEmail(context)
        val user = userService.findByEmail(email)
        val adoptedAnimal = animalService.adopt(id, user.id ?: throw IllegalArgumentException())
        context.json(adoptedAnimal).status(HttpStatus.OK_200)
    }

    fun findAllAnimals(context: Context) {
        val animals = animalService.getAll()
        when {
            context.queryParam("status") != null -> return findAnimalsByStatus(context)
            context.queryParam("specie") != null -> return findAnimalsBySpecie(context)
            context.queryParam("name") != null -> return(findAnimalsByName(context))
        }
        context.json(animals).status(HttpStatus.OK_200)
    }

    private fun findAnimalsByStatus(context: Context) {
        val name = context.queryParam("status").toString()
        val animals = animalService.getByStatus(AnimalStatus.valueOf(name.uppercase()))
        context.json(animals).status(HttpStatus.OK_200)
    }

    private fun findAnimalsBySpecie(context: Context) {
        val specie = context.queryParam("specie").toString()
        val animals = animalService.getBySpecie(Specie.valueOf(specie.uppercase()))
        context.json(animals).status(HttpStatus.OK_200)
    }

    private fun findAnimalsByName(context: Context) {
        val name = context.queryParam("name").toString()
        val animals = animalService.getByName(name)
        context.json(animals).status(HttpStatus.OK_200)
    }
}
