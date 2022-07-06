package com.abrigo.itimalia.resources.password.bcrypt

import at.favre.lib.crypto.bcrypt.BCrypt
import com.abrigo.itimalia.application.config.EnvironmentConfig
import com.abrigo.itimalia.domain.services.PasswordService

class BCryptPasswordService : PasswordService {
    override fun encode(password: String): String = BCrypt.withDefaults().hashToString(EnvironmentConfig.hashSalt().toInt(), password.toCharArray())

    override fun verify(password: String, encodedPassword: String): Boolean {
        val bcryptResult = BCrypt.verifyer().verify(password.toCharArray(), encodedPassword)
        return bcryptResult.verified
    }
}
