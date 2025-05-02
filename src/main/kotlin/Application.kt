package com.example

import com.example.com.example.blogtown.config.JwtConfig
import com.example.com.example.blogtown.di.*
import com.example.com.example.blogtown.domain.service.AuthService
import com.example.com.example.blogtown.web.plugins.*
import com.example.com.example.blogtown.web.routes.configureAuthRoutes
import com.example.com.example.blogtown.web.routes.configureBlogRoutes
// import com.example.com.example.blogtown.web.routes.configureOAuthRoutes
import com.example.com.example.blogtown.web.routes.configureUserRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
// import io.ktor.http.*
import org.koin.ktor.plugin.Koin
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val logger = LoggerFactory.getLogger(this::class.java)

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    install(Koin) {
        modules(
            appModule,
            createConfigModule(this@module),
            repositoryModule,
            serviceModule
        )
    }

    // Get dependencies from Koin
    val jwtConfig by inject<JwtConfig>()
    val authService by inject<AuthService>()

//    val redirects = mutableMapOf<String, String>()
    install(Authentication){
        // JWT
        jwt("auth-jwt") {
            realm = jwtConfig.realm
            verifier(jwtConfig.verifier)
            validate { credential ->
                authService.validateToken(credential)
            }
        }

        // OAuth authentication with google
//        oauth("auth-oauth-google") {
//            urlProvider = { "http://localhost:8080/oauth/callback" }
//            providerLookup = {
//                OAuthServerSettings.OAuth2ServerSettings(
//                    name = "google",
//                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
//                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
//                    requestMethod = HttpMethod.Post,
//                    clientId = System.getenv("GOOGLE_CLIENT_ID"),
//                    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
//                    defaultScopes = listOf(
//                        "https://www.googleapis.com/auth/userinfo.profile",
//                        "https://www.googleapis.com/auth/userinfo.email"
//                    ),
//                    extraAuthParameters = listOf("access_type" to "offline"),
//                    onStateCreated = { call, state ->
//                        //saves new state with redirect url value
//                        call.request.queryParameters["redirectUrl"]?.let {
//                            redirects[state] = it
//                        }
//                    }
//                )
//            }
//        }

        // OAuth authentication for Github
//        oauth("auth-oauth-github") {
//            urlProvider = { "http://localhost:8080/oauth/callback" }
//            providerLookup = {
//                OAuthServerSettings.OAuth2ServerSettings(
//                    name = "github",
//                    authorizeUrl = "https://github.com/login/oauth/authorize",
//                    accessTokenUrl = "https://github.com/login/oauth/access_token",
//                    requestMethod = HttpMethod.Post,
//                    clientId = this@module.environment.config.property("oauth.providers.github.clientId").getString(),
//                    clientSecret = this@module.environment.config.property("oauth.providers.github.clientSecret").getString(),
//                    defaultScopes = listOf("user:email"),
//                )
//            }
//        }
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