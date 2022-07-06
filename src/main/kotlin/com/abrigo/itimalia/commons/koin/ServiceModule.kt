package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.domain.entities.user.NewUserRequest
import com.abrigo.itimalia.domain.entities.user.UserLoginRequest
import com.abrigo.itimalia.domain.entities.user.UserRequest
import com.abrigo.itimalia.domain.services.AdminService
import com.abrigo.itimalia.domain.services.AdminServiceImpl
import com.abrigo.itimalia.domain.services.AnimalService
import com.abrigo.itimalia.domain.services.AnimalServiceImpl
import com.abrigo.itimalia.domain.services.UserService
import com.abrigo.itimalia.domain.services.UserServiceImpl
import com.abrigo.itimalia.domain.validation.Validator
import com.abrigo.itimalia.domain.validation.impl.AnimalRequestValidator
import com.abrigo.itimalia.domain.validation.impl.NewAnimalRequestValidator
import com.abrigo.itimalia.domain.validation.impl.NewUserRequestValidator
import com.abrigo.itimalia.domain.validation.impl.UserDTORequestValidator
import com.abrigo.itimalia.domain.validation.impl.UserLoginRequestValidator
import com.abrigo.itimalia.resources.validation.hibernate.HibernateValidator
import com.abrigo.itimalia.resources.validation.hibernate.gateways.AnimalRequestConstraintValidator
import com.abrigo.itimalia.resources.validation.hibernate.gateways.NewAnimalRequestConstraintValidator
import com.abrigo.itimalia.resources.validation.hibernate.gateways.NewUserRequestConstraintValidator
import com.abrigo.itimalia.resources.validation.hibernate.gateways.UserLoginRequestConstraintValidator
import com.abrigo.itimalia.resources.validation.hibernate.gateways.UserRequestConstraintValidator
import com.abrigo.itimalia.resources.validation.hibernate.utils.MapMounter
import org.koin.dsl.module.module

val serviceModule = module {
    single {
        HibernateValidator.create()
    }

    single {
        MapMounter()
    }

    single {
        UserServiceImpl(
            get(),
            get(),
            NewUserRequestValidator(NewUserRequestConstraintValidator(get(), get())) as Validator<NewUserRequest>,
            UserDTORequestValidator(UserRequestConstraintValidator(get(), get())) as Validator<UserRequest>,
            UserLoginRequestValidator(UserLoginRequestConstraintValidator(get(), get())) as Validator<UserLoginRequest>,
            get()
        ) as UserService
    }
    single { AdminServiceImpl(get(), get(), get()) as AdminService }
    single {
        AnimalServiceImpl(
            get(),
            NewAnimalRequestValidator(NewAnimalRequestConstraintValidator(get(), get())),
            AnimalRequestValidator(AnimalRequestConstraintValidator(get(), get()))
        ) as AnimalService
    }
}
