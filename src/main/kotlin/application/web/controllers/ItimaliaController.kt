package application.web.controllers

import domain.entities.NewUser
import domain.exceptions.UserNotFoundException
import domain.exceptions.ValidationException
import domain.services.UserService
import io.javalin.Context
import org.eclipse.jetty.http.HttpStatus

class ItimaliaController(private val userService: UserService){

    fun findUser(context:Context){
        try{
            val id:Int=context.pathParam("id").toInt()
            val user=userService.get(id)
            context.json(user).status(HttpStatus.OK_200)
        }
        catch (exception: ValidationException){
            context.json(exception.userResponseMessage()).status(exception.httpStatus())
            return
        }
        catch (exception: UserNotFoundException){
            context.json(exception.userResponseMessage()).status(exception.httpStatus())
            return
        }

    }

    fun addUser(context: Context){
        val newUser=context.body<NewUser>()
        val addedUser=userService.add(newUser)
        context.json(addedUser).status(HttpStatus.OK_200)
    }
}