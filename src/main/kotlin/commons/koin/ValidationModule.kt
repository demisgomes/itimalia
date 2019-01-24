package commons.koin

import domain.validation.UserValidation
import org.koin.dsl.module.module

val validationModule = module{
    single{ UserValidation() }
}