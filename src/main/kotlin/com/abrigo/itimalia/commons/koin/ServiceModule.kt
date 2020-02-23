package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.domain.entities.user.NewUserRequest
import com.abrigo.itimalia.domain.entities.user.UserDTORequest
import com.abrigo.itimalia.domain.entities.user.UserLoginRequest
import com.abrigo.itimalia.domain.services.AdminService
import com.abrigo.itimalia.domain.services.AdminServiceImpl
import com.abrigo.itimalia.domain.services.AnimalService
import com.abrigo.itimalia.domain.services.AnimalServiceImpl
import com.abrigo.itimalia.domain.services.UserService
import com.abrigo.itimalia.domain.services.UserServiceImpl
import com.abrigo.itimalia.domain.validation.Validator
import com.abrigo.itimalia.resources.validation.hibernate.AnimalDTORequestValidator
import com.abrigo.itimalia.resources.validation.hibernate.NewAnimalRequestValidator
import com.abrigo.itimalia.resources.validation.hibernate.NewUserRequestValidator
import com.abrigo.itimalia.resources.validation.hibernate.UserDTORequestValidator
import com.abrigo.itimalia.resources.validation.hibernate.UserLoginRequestValidator
import org.koin.dsl.module.module


val serviceModule = module{
    single {
        UserServiceImpl(
            get(),
            get(),
            NewUserRequestValidator(get()) as Validator<NewUserRequest>,
            UserDTORequestValidator(get()) as Validator<UserDTORequest>,
            UserLoginRequestValidator(get()) as Validator<UserLoginRequest>
        ) as UserService }
    single { AdminServiceImpl(get(), get ()) as AdminService }
    single { AnimalServiceImpl(get(), NewAnimalRequestValidator(get()), AnimalDTORequestValidator(get())) as AnimalService }
}