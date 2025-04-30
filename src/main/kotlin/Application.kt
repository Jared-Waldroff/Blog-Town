package com.example

import com.example.com.example.blogtown.services.AuthServiceImpl
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
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.plugin.Koin
import org.koin.ktor.ext.inject

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureHTTP()
    configureMonitoring()
    configureRouting()

    // Logging setup
    install(CallLogging) {
        level = Level.INFO
    }

    // JSON Serialization
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    // Error Handling
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(
                text = "500: ${cause.message}",
                status = HttpStatusCode.InternalServerError
            )
        }
    }

    // Koin dependency injection
    install(Koin) {
        modules(appModule)
    }

    // Initialize JWT config
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

}
