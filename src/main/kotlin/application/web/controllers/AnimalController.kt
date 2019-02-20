package application.web.controllers

import domain.entities.AnimalDTO
import domain.entities.AnimalStatus
import domain.entities.NewAnimal
import domain.entities.Specie
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

    fun findAllAnimals(context: Context){
        try{
            val animals=animalService.getAll()
            when {
                context.queryParam("status")!=null -> return findAnimalsByStatus(context)
                context.queryParam("specie")!=null -> return findAnimalsBySpecie(context)
                context.queryParam("name")!=null -> return(findAnimalsByName(context))
            }
            context.json(animals).status(HttpStatus.OK_200)
        }
        catch (exception:AnimalNotFoundException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
    }

    fun findAnimalsByStatus(context: Context){
        try{
            val name=context.queryParam("status").toString()
            val animals=animalService.getByStatus(AnimalStatus.valueOf(name.toUpperCase()))
            context.json(animals).status(HttpStatus.OK_200)
        }
        catch (exception:AnimalNotFoundException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
    }

    fun findAnimalsBySpecie(context: Context){
        try{
            val specie=context.queryParam("specie").toString()
            val animals=animalService.getBySpecie(Specie.valueOf(specie.toUpperCase()))
            context.json(animals).status(HttpStatus.OK_200)
        }
        catch (exception:AnimalNotFoundException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
    }

    fun findAnimalsByName(context: Context){
        try{
            val name=context.queryParam("name").toString()
            val animals=animalService.getByName(name)
            context.json(animals).status(HttpStatus.OK_200)
        }
        catch (exception:AnimalNotFoundException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
    }
}
