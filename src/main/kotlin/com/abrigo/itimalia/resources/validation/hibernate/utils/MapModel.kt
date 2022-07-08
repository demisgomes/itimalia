package com.abrigo.itimalia.resources.validation.hibernate.utils

import com.abrigo.itimalia.domain.entities.animal.AnimalRequest
import com.abrigo.itimalia.domain.entities.animal.NewAnimalRequest
import com.abrigo.itimalia.domain.entities.user.NewUserRequest
import com.abrigo.itimalia.domain.entities.user.UserLoginRequest
import com.abrigo.itimalia.domain.entities.user.UserRequest
import com.abrigo.itimalia.domain.exceptions.ValidationException
import com.abrigo.itimalia.resources.validation.hibernate.Model
import com.abrigo.itimalia.resources.validation.hibernate.entities.AnimalRequestModel
import com.abrigo.itimalia.resources.validation.hibernate.entities.NewAnimalRequestModel
import com.abrigo.itimalia.resources.validation.hibernate.entities.NewUserRequestModel
import com.abrigo.itimalia.resources.validation.hibernate.entities.UserLoginRequestModel
import com.abrigo.itimalia.resources.validation.hibernate.entities.UserRequestModel

object MapModel {

    fun <T> getModel(t: T): Model {
        return when (t) {
            is AnimalRequest -> AnimalRequestModel(
                t.id,
                t.name,
                t.age,
                t.timeUnit,
                t.specie,
                t.description,
                t.creationDate,
                t.modificationDate,
                t.status,
                t.deficiencies,
                t.sex,
                t.size,
                t.castrated,
                t.createdById
            )

            is NewAnimalRequest -> NewAnimalRequestModel(
                t.name,
                t.age,
                t.timeUnit,
                t.specie,
                t.description,
                t.deficiencies,
                t.sex,
                t.size,
                t.castrated
            )

            is NewUserRequest -> NewUserRequestModel(t.email, t.password, t.birthDate, t.gender, t.name, t.phone)

            is UserLoginRequest -> UserLoginRequestModel(t.email, t.password)

            is UserRequest -> UserRequestModel(t.id, t.email, t.password, t.birthDate, t.gender, t.name, t.phone, t.role, t.creationDate, t.modificationDate, t.token)
            else -> throw ValidationException(hashMapOf("unexpectedError: " to mutableListOf("An unexpected error occurred in validation")))
        }
    }
}
