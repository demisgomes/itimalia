package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.domain.entities.user.NewUserRequest
import com.abrigo.itimalia.domain.entities.user.UserDTORequest
import com.abrigo.itimalia.domain.entities.user.UserLoginRequest
import com.abrigo.itimalia.domain.entities.user.toNewUser
import com.abrigo.itimalia.domain.entities.user.toUserDTO
import com.abrigo.itimalia.domain.entities.user.toUserLogin
import com.abrigo.itimalia.domain.entities.user.toUserSearched
import com.abrigo.itimalia.domain.jwt.JWTAccessManager
import com.abrigo.itimalia.domain.services.UserService
import com.abrigo.itimalia.domain.validation.Validator
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus


class UserController(
    private val userService: UserService,
    private val jwtAccessManager: JWTAccessManager,
    private val validatorNewUser: Validator<NewUserRequest>,
    private val validatorUserDTO: Validator<UserDTORequest>,
    private val validatorUserLogin: Validator<UserLoginRequest>){

    fun findUser(context: Context){
        val id:Int=context.pathParam("id").toInt()
        val user=userService.get(id)
        context.json(user.toUserSearched())
        context.status(HttpStatus.OK_200)
    }

    fun addUser(context: Context){
        val newUserRequest= context.body<NewUserRequest>()
        validatorNewUser.validate(newUserRequest)
        val newUser = newUserRequest.toNewUser()
        val addedUser=userService.add(newUser)
        context.json(addedUser).status(HttpStatus.CREATED_201)
    }

    fun updateUser(context: Context){
        val id:Int=context.pathParam("id").toInt()
        val modifiedUserRequest= context.body<UserDTORequest>()
        validatorUserDTO.validate(modifiedUserRequest)
        val modifiedUser = modifiedUserRequest.toUserDTO()
        val returnedUser=userService.update(id, modifiedUser, jwtAccessManager.extractRole(context), jwtAccessManager.extractEmail(context))
        context.json(returnedUser).status(HttpStatus.OK_200)
    }

    fun deleteUser(context: Context){
        val id:Int=context.pathParam("id").toInt()
        userService.delete(id, jwtAccessManager.extractRole(context), jwtAccessManager.extractEmail(context))
        context.status(HttpStatus.NO_CONTENT_204)
    }

    fun loginUser(context: Context){
        val newUserLoginRequest=context.body<UserLoginRequest>()
        validatorUserLogin.validate(newUserLoginRequest)
        val newUserLogin = newUserLoginRequest.toUserLogin()
        val userLogged=userService.login(newUserLogin)
        context.json(userLogged).status(HttpStatus.OK_200)
    }
}