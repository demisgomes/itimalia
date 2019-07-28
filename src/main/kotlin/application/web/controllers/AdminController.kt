package application.web.controllers

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import domain.entities.NewUser
import domain.exceptions.EmailAlreadyExistsException
import domain.exceptions.InvalidGenderException
import domain.exceptions.UserNotFoundException
import domain.exceptions.ValidationException
import domain.jwt.JWTAccessManager
import domain.services.AdminService
import io.javalin.Context
import org.eclipse.jetty.http.HttpStatus

class AdminController(private val adminService: AdminService, private val jwtAccessManager: JWTAccessManager){
    fun addAdminUser(context: Context){
        try{
            val newUser=context.body<NewUser>()
            val addedUser=adminService.add(newUser)
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
        catch (exception: UserNotFoundException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
    }
}