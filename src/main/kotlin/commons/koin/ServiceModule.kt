package commons.koin

import domain.repositories.UserRepositoryImpl
import domain.services.UserServiceImpl
import org.koin.dsl.module.module


val serviceModule = module{
    single{ UserRepositoryImpl() }
    single{ UserServiceImpl() }
}