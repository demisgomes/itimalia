package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.domain.entities.AnimalDTO
import com.abrigo.itimalia.domain.entities.AnimalStatus
import com.abrigo.itimalia.domain.entities.NewAnimal
import com.abrigo.itimalia.domain.entities.Specie
import com.abrigo.itimalia.domain.services.AnimalService
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus

class AnimalController(private val animalService: AnimalService) {
    fun addAnimal(context: Context) {
        val newAnimal=context.body<NewAnimal>()
        val newAnimalDTO=animalService.add(newAnimal)
        context.json(newAnimalDTO).status(HttpStatus.CREATED_201)
    }

    fun findAnimal(context: Context) {
        val id:Int=context.pathParam("id").toInt()
        val animal=animalService.get(id)
        context.json(animal).status(HttpStatus.OK_200)
    }

    fun updateAnimal(context: Context){
        val id:Int=context.pathParam("id").toInt()
        val animalToBeUpdated = context.body<AnimalDTO>()

        animalService.get(id)
        val updatedAnimal=animalService.update(id, animalToBeUpdated)
        context.json(updatedAnimal).status(HttpStatus.OK_200)
    }

    fun deleteAnimal(context: Context) {
        val id:Int=context.pathParam("id").toInt()
        animalService.get(id)
        animalService.delete(id)
        context.status(HttpStatus.NO_CONTENT_204)
    }

    fun adopt(context: Context) {
        val id:Int=context.pathParam("id").toInt()
        val adoptedAnimal=animalService.adopt(id)
        context.json(adoptedAnimal).status(HttpStatus.OK_200)
    }

    fun findAllAnimals(context: Context){
        val animals=animalService.getAll()
        when {
            context.queryParam("status")!=null -> return findAnimalsByStatus(context)
            context.queryParam("specie")!=null -> return findAnimalsBySpecie(context)
            context.queryParam("name")!=null -> return(findAnimalsByName(context))
        }
        context.json(animals).status(HttpStatus.OK_200)
    }

    private fun findAnimalsByStatus(context: Context){
        val name=context.queryParam("status").toString()
        val animals=animalService.getByStatus(AnimalStatus.valueOf(name.toUpperCase()))
        context.json(animals).status(HttpStatus.OK_200)

    }

    private fun findAnimalsBySpecie(context: Context){
        val specie=context.queryParam("specie").toString()
        val animals=animalService.getBySpecie(Specie.valueOf(specie.toUpperCase()))
        context.json(animals).status(HttpStatus.OK_200)
    }

    private fun findAnimalsByName(context: Context){
        val name=context.queryParam("name").toString()
        val animals=animalService.getByName(name)
        context.json(animals).status(HttpStatus.OK_200)
    }
}
