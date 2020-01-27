package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.domain.validation.UserLoginValidator
import com.abrigo.itimalia.domain.validation.UserValidator
import org.koin.dsl.module.module

val validationModule = module{
    single { UserValidator() }
    single { UserLoginValidator() }

}