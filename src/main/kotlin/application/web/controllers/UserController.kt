package application.web.controllers

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import domain.entities.NewUser
import domain.entities.Roles
import domain.entities.UserDTO
import domain.entities.UserLogin
import domain.exceptions.*
import domain.jwt.JWTAccessManager
import domain.services.UserService
import io.javalin.Context
import org.eclipse.jetty.http.HttpStatus

class UserController(private val userService: UserService, private val jwtAccessManager: JWTAccessManager){

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
        catch (exception:InvalidFormatException){
            val invalidGenderException=InvalidGenderException()
            context.json(invalidGenderException.createErrorResponse()).status(invalidGenderException.httpStatus())
        }
    }

    fun updateUser(context: Context){
        try{
            val id:Int=context.pathParam("id").toInt()
            val modifiedUser=context.body<UserDTO>()
            val returnedUser=userService.update(id,modifiedUser, jwtAccessManager.extractRole(context), jwtAccessManager.extractEmail(context))
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
        catch (exception:UnauthorizedRoleChangeException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
        catch (exception:UnauthorizedDifferentUserChangeException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
    }

    fun deleteUser(context: Context){
        try{
            val id:Int=context.pathParam("id").toInt()
            userService.delete(id, jwtAccessManager.extractRole(context), jwtAccessManager.extractEmail(context))
            context.status(HttpStatus.NO_CONTENT_204)
        }
        catch (exception:UserNotFoundException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
        catch (exception:UnauthorizedDifferentUserChangeException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
    }

    fun loginUser(context: Context){
        try{
            val newUserLogin=context.body<UserLogin>()
            val userLogged=userService.login(newUserLogin)
            context.json(userLogged).status(HttpStatus.OK_200)
            
        }
        catch (exception:ValidationException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
        catch (exception:InvalidCredentialsException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
        }
    }

//    private fun updateCallByAdmin(context:Context, id:Int, modifiedUser:UserDTO){
//        val returnedUser=userService.update(id,modifiedUser, Roles.ADMIN, jwtAccessManager.)
//        context.json(returnedUser).status(HttpStatus.OK_200)
//    }

}