package application.web.controllers

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import domain.entities.NewUser
import domain.exceptions.EmailAlreadyExistsException
import domain.exceptions.InvalidGenderException
import domain.exceptions.ValidationException
import domain.jwt.JWTAccessManager
import domain.services.UserService
import io.javalin.Context
import org.eclipse.jetty.http.HttpStatus

class AdminController(private val userService: UserService, private val jwtAccessManager: JWTAccessManager){
    fun addAdminUser(context: Context){
        try{
            val newUser=context.body<NewUser>()
            val addedUser=userService.add(newUser)
            context.json(addedUser).status(HttpStatus.CREATED_201)
        }
        catch (exception: ValidationException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
        catch (exception: InvalidFormatException){
            val invalidGenderException= InvalidGenderException()
            context.json(invalidGenderException.createErrorResponse()).status(invalidGenderException.httpStatus())
        }
        catch (exception: EmailAlreadyExistsException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
    }
}