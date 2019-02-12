package commons.koin

import domain.repositories.UserRepository
import domain.repositories.UserRepositoryImpl
import domain.services.AdminService
import domain.services.AdminServiceImpl
import domain.services.UserService
import domain.services.UserServiceImpl
import org.koin.dsl.module.module


val serviceModule = module{
    single{ UserRepositoryImpl() as UserRepository }
    single{ UserServiceImpl(get(), get()) as UserService }
    single { AdminServiceImpl(get(), get ()) as AdminService }
}