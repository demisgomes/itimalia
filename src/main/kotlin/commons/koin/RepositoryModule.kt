package commons.koin

import domain.repositories.AnimalRepository
import domain.repositories.AnimalRepositoryImpl
import domain.repositories.UserRepository
import domain.repositories.UserRepositoryImpl
import org.koin.dsl.module.module

val repositoryModule = module {
    single{ UserRepositoryImpl() as UserRepository }
    single { AnimalRepositoryImpl() as AnimalRepository }
}