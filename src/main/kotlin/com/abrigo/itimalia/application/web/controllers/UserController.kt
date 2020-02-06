package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.domain.entities.user.*
import com.abrigo.itimalia.domain.jwt.JWTAccessManager
import com.abrigo.itimalia.domain.services.UserService
import com.abrigo.itimalia.domain.validation.Validator
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus


class UserController(private val userService: UserService, private val jwtAccessManager: JWTAccessManager, private val validator: Validator<NewUserRequest>){

    fun findUser(context: Context){
        val id:Int=context.pathParam("id").toInt()
        val user=userService.get(id)
        context.json(user.toUserSearched())
        context.status(HttpStatus.OK_200)
    }

    fun addUser(context: Context){
        val newUserRequest= context.body<NewUserRequest>()
        validator.validate(newUserRequest)

        val newUser = NewUser(
            newUserRequest.email!!,
            newUserRequest.password!!,
            newUserRequest.birthDate,
            newUserRequest.gender,
            newUserRequest.name!!,
            newUserRequest.phone!!
        )

        val addedUser=userService.add(newUser)
        context.json(addedUser).status(HttpStatus.CREATED_201)
    }

    fun updateUser(context: Context){
        val id:Int=context.pathParam("id").toInt()
        val modifiedUser=context.body<UserDTO>()
        val returnedUser=userService.update(id,modifiedUser, jwtAccessManager.extractRole(context), jwtAccessManager.extractEmail(context))
        context.json(returnedUser).status(HttpStatus.OK_200)
    }

    fun deleteUser(context: Context){
        val id:Int=context.pathParam("id").toInt()
        userService.delete(id, jwtAccessManager.extractRole(context), jwtAccessManager.extractEmail(context))
        context.status(HttpStatus.NO_CONTENT_204)
    }

    fun loginUser(context: Context){
        val newUserLogin=context.body<UserLogin>()
        val userLogged=userService.login(newUserLogin)
        context.json(userLogged).status(HttpStatus.OK_200)
    }
}