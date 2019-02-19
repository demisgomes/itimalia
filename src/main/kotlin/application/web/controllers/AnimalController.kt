package application.web.controllers

import domain.entities.AnimalDTO
import domain.entities.NewAnimal
import domain.exceptions.*
import domain.services.AnimalService
import io.javalin.Context
import org.eclipse.jetty.http.HttpStatus

class AnimalController(private val animalService: AnimalService) {
    fun addAnimal(context: Context) {
        try{
            val newAnimal=context.body<NewAnimal>()
            val newAnimalDTO=animalService.add(newAnimal)
            context.json(newAnimalDTO).status(HttpStatus.CREATED_201)
        }
        catch (exception:ValidationException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }

    }

    fun findAnimal(context: Context) {
        try{
            val id:Int=context.pathParam("id").toInt()
            val animal=animalService.get(id)
            context.json(animal).status(HttpStatus.OK_200)
        }
        catch (exception:AnimalNotFoundException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
    }

    fun updateAnimal(context: Context){
        try{
            val id:Int=context.pathParam("id").toInt()
            val animalToBeUpdated = context.body<AnimalDTO>()

            animalService.get(id)
            val updatedAnimal=animalService.update(id, animalToBeUpdated)
            context.json(updatedAnimal).status(HttpStatus.OK_200)
        }
        catch (exception:AnimalNotFoundException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
        catch (exception:ValidationException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
    }

    fun deleteAnimal(context: Context) {
        try{
            val id:Int=context.pathParam("id").toInt()
            animalService.get(id)
            animalService.delete(id)
            context.status(HttpStatus.NO_CONTENT_204)
        }
        catch (exception:AnimalNotFoundException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }

    }

    fun adopt(context: Context) {
        try{
            val id:Int=context.pathParam("id").toInt()
            val adoptedAnimal=animalService.adopt(id)
            context.json(adoptedAnimal).status(HttpStatus.OK_200)
        }
        catch (exception:AnimalNotFoundException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
        catch (exception:AnimalAlreadyAdoptedException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
        catch (exception: AnimalDeadException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
        catch (exception:AnimalGoneException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
    }

}
