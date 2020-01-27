package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.application.web.controllers.AdminController
import com.abrigo.itimalia.application.web.controllers.AnimalController
import com.abrigo.itimalia.application.web.controllers.UserController
import com.abrigo.itimalia.domain.entities.user.NewUser
import com.abrigo.itimalia.domain.entities.user.NewUserRequest
import com.abrigo.itimalia.domain.validation.NewUserValidator
import com.abrigo.itimalia.domain.validation.Validator
import com.abrigo.itimalia.domain.validation.hibernate.HibernateValidator
import org.koin.dsl.module.module
import javax.validation.Validation
import javax.validation.Validation.buildDefaultValidatorFactory



val controllerModule = module {
    single {
        HibernateValidator.create()
    }
    single { NewUserValidator(get()) as Validator<NewUserRequest> }
    single { UserController(get(), get(), get()) }
    single { AdminController(get(),get()) }
    single { AnimalController(get()) }
}