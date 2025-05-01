package com.example

import com.example.com.example.blogtown.config.JwtConfig
import com.example.com.example.blogtown.di.*
import com.example.com.example.blogtown.domain.service.AuthService
import com.example.com.example.blogtown.web.plugins.*
import com.example.com.example.blogtown.web.routes.configureAuthRoutes
import com.example.com.example.blogtown.web.routes.configureBlogRoutes
import com.example.com.example.blogtown.web.routes.configureUserRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val logger = LoggerFactory.getLogger(this::class.java)

    // JSON Serialization
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    // Koin dependency injection
    install(Koin) {
        logger.info("Initializing Koin dependency injection")
        modules(appModule, createConfigModule(this@module), repositoryModule, serviceModule)
    }

    // Get dependencies from Koin
    val jwtConfig by inject<JwtConfig>()
    val authService by inject<AuthService>()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtConfig.realm
            verifier(jwtConfig.verifier)
            validate { credential ->
                authService.validateToken(credential)
            }
        }
    }

    configureRouting()
    configureSerialization()
    configureHTTP()
    configureMonitoring()

    configureAuthRoutes()
    configureBlogRoutes()
    configureUserRoutes()

    logger.info("Application started successfully")
}