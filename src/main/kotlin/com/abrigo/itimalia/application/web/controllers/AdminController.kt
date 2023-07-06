package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.toLoggedUser
import com.abrigo.itimalia.domain.exceptions.InvalidGenderException
import com.abrigo.itimalia.domain.services.AdminService
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus

class AdminController(private val adminService: AdminService) {
    fun addAdminUser(context: Context) {
        try {
            val newUser = context.bodyAsClass<NewUser>()
            val addedUser = adminService.add(newUser)
            context.json(addedUser.toLoggedUser())
            context.status(HttpStatus.CREATED_201)
        } catch (exception: InvalidFormatException) {
            val invalidGenderException = InvalidGenderException()
            context.json(invalidGenderException.createErrorResponse()).status(invalidGenderException.httpStatus())
        }
    }
}
