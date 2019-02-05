package commons.koin

import domain.jwt.JWTUtils
import org.koin.dsl.module.module

val JWTModule = module{
    single{ JWTUtils() }
}