package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.application.web.controllers.AdminController
import com.abrigo.itimalia.application.web.controllers.AnimalController
import com.abrigo.itimalia.application.web.controllers.UserController
import com.abrigo.itimalia.resources.validation.hibernate.HibernateValidator
import com.abrigo.itimalia.resources.validation.hibernate.NewUserRequestValidator
import com.abrigo.itimalia.resources.validation.hibernate.UserDTORequestValidator
import com.abrigo.itimalia.resources.validation.hibernate.UserLoginRequestValidator
import org.koin.dsl.module.module


val controllerModule = module {
    single {
        HibernateValidator.create()
    }
    single {
        UserController(
            get(),
            get(),
            NewUserRequestValidator(get()),
            UserDTORequestValidator(get()),
            UserLoginRequestValidator(get())
        )
    }
    single { AdminController(get(),get()) }
    single { AnimalController(get()) }
}