package application.web.controllers

import domain.entities.NewUser
import domain.entities.UserDTO
import domain.exceptions.UnmodifiedUserException
import domain.exceptions.UserNotFoundException
import domain.exceptions.ValidationException
import domain.services.UserService
import io.javalin.Context
import org.eclipse.jetty.http.HttpStatus

class UserController(private val userService: UserService){

    fun findUser(context:Context){
        try{
            val id:Int=context.pathParam("id").toInt()
            val user=userService.get(id)
            context.json(user).status(HttpStatus.OK_200)
        }
        catch (exception: UserNotFoundException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
            return
        }

    }

    fun addUser(context: Context){
        try{
            val newUser=context.body<NewUser>()
            val addedUser=userService.add(newUser)
            context.json(addedUser).status(HttpStatus.CREATED_201)
        }
        catch (exception: ValidationException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
    }

    fun updateUser(context: Context){
        try{
            val id:Int=context.pathParam("id").toInt()
            val modifiedUser=context.body<UserDTO>()
            val returnedUser=userService.update(id,modifiedUser)
            context.json(returnedUser).status(HttpStatus.OK_200)
        }
        catch (exception: ValidationException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
        catch (exception:UnmodifiedUserException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
        catch (exception:UserNotFoundException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
    }

    fun deleteUser(context: Context){
        try{
            val id:Int=context.pathParam("id").toInt()
            val deletedUser=userService.delete(id)
            context.json(deletedUser).status(HttpStatus.OK_200)
        }
        catch (exception:UserNotFoundException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
    }
}