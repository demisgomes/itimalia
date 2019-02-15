package application.web.controllers

import domain.entities.NewAnimal
import domain.exceptions.InvalidNameException
import domain.exceptions.InvalidSpecieException
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
        catch (exception:InvalidSpecieException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
        catch (exception:InvalidNameException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }

    }

}
