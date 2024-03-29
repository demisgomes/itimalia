package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.domain.services.PasswordService
import com.abrigo.itimalia.resources.password.bcrypt.BCryptPasswordService
import org.koin.dsl.module

val passwordModule = module {
    single { BCryptPasswordService(get()) as PasswordService }
}
