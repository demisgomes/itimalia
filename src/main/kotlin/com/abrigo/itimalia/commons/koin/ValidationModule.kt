package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.domain.entities.animal.AnimalRequest
import com.abrigo.itimalia.domain.entities.animal.NewAnimalRequest
import com.abrigo.itimalia.domain.entities.user.NewUserRequest
import com.abrigo.itimalia.domain.entities.user.UserLoginRequest
import com.abrigo.itimalia.domain.entities.user.UserRequest
import com.abrigo.itimalia.domain.validation.Validator
import com.abrigo.itimalia.domain.validation.impl.ValidatorRequest
import com.abrigo.itimalia.resources.validation.hibernate.HibernateValidator
import com.abrigo.itimalia.resources.validation.hibernate.Model
import com.abrigo.itimalia.resources.validation.hibernate.gateways.ConstraintModelValidator
import org.koin.dsl.module.module

val validationModule = module {
    single {
        HibernateValidator.create()
    }

    single {
        ConstraintModelValidator<Model>(get())
    }

    single {
        ValidatorRequest<NewUserRequest>(get()) as Validator<NewUserRequest>
    }
    single {
        ValidatorRequest<UserRequest>(get()) as Validator<UserRequest>
    }
    single {
        ValidatorRequest<UserLoginRequest>(get()) as Validator<UserLoginRequest>
    }
    single {
        ValidatorRequest<NewAnimalRequest>(get()) as Validator<NewAnimalRequest>
    }
    single {
        ValidatorRequest<AnimalRequest>(get()) as Validator<AnimalRequest>
    }
}
