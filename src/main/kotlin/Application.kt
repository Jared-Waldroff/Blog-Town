package com.example

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level
import com.example.com.example.blogtown.config.JwtConfig
import com.example.com.example.blogtown.di.appModule
import com.example.com.example.blogtown.domain.service.AuthService
import com.example.com.example.blogtown.web.plugins.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.plugin.Koin
import org.koin.ktor.ext.inject
import com.example.com.example.blogtown.web.routes.configureAuthRoutes

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureHTTP()
    configureMonitoring()

    val jwtConfig = JwtConfig(environment.config)
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
    configureAuthRoutes()


    // JSON Serialization
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    // Koin dependency injection
    install(Koin) {
        modules(appModule)
    }
}
