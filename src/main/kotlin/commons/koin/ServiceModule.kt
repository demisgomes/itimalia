package commons.koin

import domain.services.*
import org.koin.dsl.module.module


val serviceModule = module{
    single{ UserServiceImpl(get(), get()) as UserService }
    single { AdminServiceImpl(get(), get ()) as AdminService }
    single { AnimalServiceImpl(get()) as AnimalService }
}