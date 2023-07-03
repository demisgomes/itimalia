package com.abrigo.itimalia.application.web.controllers

import com.abrigo.itimalia.application.config.EnvironmentConfig
import com.abrigo.itimalia.application.web.accessmanagers.JWTAccessManager
import com.abrigo.itimalia.application.web.extensions.context.queryParamAsEnum
import com.abrigo.itimalia.domain.entities.animal.AnimalRequest
import com.abrigo.itimalia.domain.entities.animal.AnimalSex
import com.abrigo.itimalia.domain.entities.animal.AnimalSize
import com.abrigo.itimalia.domain.entities.animal.AnimalStatus
import com.abrigo.itimalia.domain.entities.animal.NewAnimalRequest
import com.abrigo.itimalia.domain.entities.animal.Specie
import com.abrigo.itimalia.domain.entities.filter.FilterOptions
import com.abrigo.itimalia.domain.entities.paging.Direction
import com.abrigo.itimalia.domain.entities.paging.OrderBy
import com.abrigo.itimalia.domain.entities.paging.PagingOptions
import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.domain.services.AnimalService
import com.abrigo.itimalia.domain.services.UserService
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus

class AnimalController(
    private val animalService: AnimalService,
    private val jwtAccessManager: JWTAccessManager,
    private val userService: UserService,
    private val environmentConfig: EnvironmentConfig
) {

    fun addAnimal(context: Context) {
        val newAnimal = context.bodyAsClass<NewAnimalRequest>()
        val email = jwtAccessManager.extractEmail(context)
        val user = userService.findByEmail(email)
        val newAnimalDTO = animalService.add(newAnimal, user.id ?: throw IllegalArgumentException())
        context.json(newAnimalDTO).status(HttpStatus.CREATED_201)
    }

    fun findAnimal(context: Context) {
        val id: Int = context.pathParam("id").toInt()
        val animal = animalService.get(id)
        context.json(animal).status(HttpStatus.OK_200)
    }

    fun updateAnimal(context: Context) {
        val id: Int = context.pathParam("id").toInt()
        val animalToBeUpdated = context.bodyAsClass<AnimalRequest>()
        val updatedAnimal = animalService.update(id, animalToBeUpdated)
        context.json(updatedAnimal).status(HttpStatus.OK_200)
    }

    fun deleteAnimal(context: Context) {
        val id: Int = context.pathParam("id").toInt()
        animalService.delete(id)
        context.status(HttpStatus.NO_CONTENT_204)
    }

    fun adopt(context: Context) {
        val id: Int = context.pathParam("id").toInt()
        val email = jwtAccessManager.extractEmail(context)
        val user = userService.findByEmail(email)
        val adoptedAnimal = animalService.adopt(id, user.id ?: throw IllegalArgumentException())
        context.json(adoptedAnimal).status(HttpStatus.OK_200)
    }

    fun findAllAnimals(context: Context) {
        val pagingOptions = buildPagingOptions(context)
        val filterOptions = buildFilterOptions(context)
        val animals = animalService.getAll(filterOptions, pagingOptions)
        context.json(animals).status(HttpStatus.OK_200)
    }

    private fun buildPagingOptions(context: Context): PagingOptions {
        val limitParam = context.queryParam("limit") ?: "10"
        val pageParam = context.queryParam("page") ?: "1"
        val orderBy = context.queryParamAsEnum("order_by") ?: OrderBy.ID
        val direction = context.queryParamAsEnum("direction") ?: Direction.ASC

        val pagingOptions = PagingOptions(
            limitParam.toIntOrNull() ?: 10,
            pageParam.toIntOrNull() ?: 1,
            orderBy,
            direction
        )

        // put this as validator. We will change this when pass validator to controllers.
        validatePaging(pagingOptions)
        return pagingOptions
    }

    private fun validatePaging(pagingOptions: PagingOptions) {
        val maxPageLimit = environmentConfig.maxPageLimit().toInt()

        if (pagingOptions.page < 1) {
            throw ValidationException(mapOf("page" to mutableListOf("page must be greater than 0")))
        }

        if (pagingOptions.limit !in 1..maxPageLimit) {
            throw ValidationException(mapOf("limit" to mutableListOf("limit must be greater than 0 and lower than $maxPageLimit")))
        }
    }

    private fun buildFilterOptions(context: Context): FilterOptions {
        val status = context.queryParamAsEnum<AnimalStatus>("status")
        val specie = context.queryParamAsEnum<Specie>("specie")
        val sex = context.queryParamAsEnum<AnimalSex>("sex")
        val size = context.queryParamAsEnum<AnimalSize>("size")
        val name = context.queryParam("name") ?: ""
        val castrated = context.queryParam("castrated")

        return FilterOptions(
            name.ifBlank { null },
            specie,
            status,
            sex,
            size,
            castrated = if (castrated.isNullOrBlank()) null else castrated.toBoolean()
        )
    }
}
