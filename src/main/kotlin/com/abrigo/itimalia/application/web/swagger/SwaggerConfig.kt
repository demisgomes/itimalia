package com.abrigo.itimalia.application.web.swagger

import com.abrigo.itimalia.application.web.accessmanagers.entities.RouteRole
import io.javalin.core.JavalinConfig
import io.javalin.core.security.SecurityUtil.roles
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.javalin.plugin.openapi.ui.SwaggerOptions
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme

const val CONTENT_TYPE = "application/json"

object SwaggerConfig {

    private fun getSecurityScheme(): SecurityScheme {
        val securityScheme = SecurityScheme()
        securityScheme.type = SecurityScheme.Type.HTTP
        securityScheme.scheme = "bearer"
        securityScheme.bearerFormat = "JWT"

        return securityScheme
    }

    fun registerPlugin(config: JavalinConfig): JavalinConfig {
        return config.registerPlugin(OpenApiPlugin(getOpenApiOptions()))
    }

    private fun getOpenApiOptions(): OpenApiOptions {
        val initialConfigurationCreator = {
            OpenAPI()
                .info(Info().version("1.0").description("Itimalia Application").title("API Documentation"))
                .schemaRequirement("BearerAuth", getSecurityScheme())
        }

        val openApiOptions = OpenApiOptions(initialConfigurationCreator)
        openApiOptions.path("/swagger-docs")
        openApiOptions.swagger(SwaggerOptions("/swagger").title("Itimalia Documentation")).roles(roles(RouteRole.ANYONE))

        return openApiOptions
    }

}