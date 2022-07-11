package com.abrigo.itimalia.commons.koin

import com.abrigo.itimalia.domain.validation.ConstraintValidator
import com.abrigo.itimalia.domain.validation.Request
import com.abrigo.itimalia.domain.validation.ValidatorRequest
import com.abrigo.itimalia.domain.validation.impl.ValidatorRequestImpl
import com.abrigo.itimalia.resources.validation.hibernate.HibernateValidator
import com.abrigo.itimalia.resources.validation.hibernate.gateways.ConstraintRequestValidator
import org.koin.dsl.module

val validationModule = module {
    single {
        HibernateValidator.create()
    }

    single {
        ConstraintRequestValidator(get()) as ConstraintValidator<Request>
    }

    single {
        ValidatorRequestImpl(get()) as ValidatorRequest<Request>
    }
}
