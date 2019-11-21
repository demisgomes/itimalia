package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.exceptions.InvalidGenderException
import com.abrigo.itimalia.domain.jwt.JWTAccessManager
import com.abrigo.itimalia.domain.services.AdminService
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus

class AdminController(private val adminService: AdminService, private val jwtAccessManager: JWTAccessManager){
    fun addAdminUser(context: Context){
        try{
            val newUser=context.body<NewUser>()
            val addedUser=adminService.add(newUser, jwtAccessManager.extractRole(context))
            context.json(addedUser).status(HttpStatus.CREATED_201)
        }
        catch (exception: InvalidFormatException){
            val invalidGenderException= InvalidGenderException()
            context.json(invalidGenderException.createErrorResponse()).status(invalidGenderException.httpStatus())
        }
    }
}