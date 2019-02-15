package application.web.controllers

import domain.entities.NewAnimal
import domain.services.AnimalService
import io.javalin.Context
import org.eclipse.jetty.http.HttpStatus

class AnimalController(private val animalService: AnimalService) {
    fun addAnimal(context: Context) {
        val newAnimal=context.body<NewAnimal>()
        val newAnimalDTO=animalService.add(newAnimal)
        context.json(newAnimalDTO).status(HttpStatus.CREATED_201)
    }

}
