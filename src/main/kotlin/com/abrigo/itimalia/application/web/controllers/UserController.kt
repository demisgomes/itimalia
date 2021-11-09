package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.application.web.accessmanagers.JWTAccessManager
import com.abrigo.itimalia.domain.entities.user.NewUserRequest
import com.abrigo.itimalia.domain.entities.user.UserLoginRequest
import com.abrigo.itimalia.domain.entities.user.UserRequest
import com.abrigo.itimalia.domain.entities.user.toUserPublicInfo
import com.abrigo.itimalia.domain.services.UserService
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus


class UserController(
    private val userService: UserService,
    private val jwtAccessManager: JWTAccessManager
){

    fun findUser(context: Context){
        val id:Int=context.pathParam("id").toInt()
        val user=userService.get(id)
        context.json(user.toUserPublicInfo())
        context.status(HttpStatus.OK_200)
    }

    fun addUser(context: Context){
        val newUserRequest= context.body<NewUserRequest>()
        val addedUser=userService.add(newUserRequest)
        context.json(addedUser).status(HttpStatus.CREATED_201)
    }

    fun updateUser(context: Context){
        val id:Int=context.pathParam("id").toInt()
        val modifiedUserRequest= context.body<UserRequest>()
        val returnedUser=userService.update(id, modifiedUserRequest, jwtAccessManager.extractRole(context), jwtAccessManager.extractEmail(context))
        context.json(returnedUser).status(HttpStatus.OK_200)
    }

    fun deleteUser(context: Context){
        val id:Int=context.pathParam("id").toInt()
        userService.delete(id, jwtAccessManager.extractRole(context), jwtAccessManager.extractEmail(context))
        context.status(HttpStatus.NO_CONTENT_204)
    }

    fun loginUser(context: Context){
        val newUserLoginRequest=context.body<UserLoginRequest>()
        val userLogged=userService.login(newUserLoginRequest)
        context.json(userLogged).status(HttpStatus.OK_200)
    }
}