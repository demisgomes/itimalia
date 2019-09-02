package com.abrigo.itimalia.application.web.controllers

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.UserDTO
import com.abrigo.itimalia.domain.entities.user.UserLogin
import com.abrigo.itimalia.domain.entities.user.toUserSearched
import com.abrigo.itimalia.domain.exceptions.*
import com.abrigo.itimalia.domain.jwt.JWTAccessManager
import com.abrigo.itimalia.domain.services.UserService
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus

class UserController(private val userService: UserService, private val jwtAccessManager: JWTAccessManager){

    fun findUser(context: Context){
        try{
            val id:Int=context.pathParam("id").toInt()
            val user=userService.get(id)
            context.json(user.toUserSearched())
            context.status(HttpStatus.OK_200)
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
        catch (exception:EmailAlreadyExistsException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
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
        catch (exception:EmailAlreadyExistsException){
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
        catch (exception: UserNotFoundException){
            context.json(exception.createErrorResponse()).status(exception.httpStatus())
            return
        }
    }
}