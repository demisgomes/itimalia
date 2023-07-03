package com.abrigo.itimalia.application.config

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv

class ItimaliaDotenv(private val envFile: String? = null) {
    fun build(): Dotenv {
        if (envFile.isNullOrBlank()) return dotenv {
            ignoreIfMissing = true
        }
        return dotenv {
            filename = envFile
        }
    }
}
