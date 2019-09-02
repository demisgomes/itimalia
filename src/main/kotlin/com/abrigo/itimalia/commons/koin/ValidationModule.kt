package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.domain.validation.UserLoginValidation
import com.abrigo.itimalia.domain.validation.UserValidation
import org.koin.dsl.module.module

val validationModule = module{
    single{ UserValidation() }
    single { UserLoginValidation() }
}