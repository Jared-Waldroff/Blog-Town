package com.example.com.example.blogtown.web.routes

import com.example.com.example.blogtown.domain.model.LoginRequest
import com.example.com.example.blogtown.domain.model.TokenRefreshRequest
import com.example.com.example.blogtown.domain.model.UserCreationRequest
import com.example.com.example.blogtown.domain.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureAuthRoutes() {
    val authService by inject<AuthService>()

    routing {
        post("/create-user") {
            try {
                val request = call.receive<UserCreationRequest>()
                val user = authService.createUser(request)
                call.respond(HttpStatusCode.Created, user)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to create user"))
            }
        }

        post("/auth/login") {
            try {
                val request = call.receive<LoginRequest>()
                val tokenResponse = authService.loginUser(request)
                call.respond(HttpStatusCode.OK, tokenResponse)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Login failed: ${e.message}"))
            }
        }

        authenticate("auth-jwt") {
            post("/auth/refresh") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asString()
                    if (userId.isNullOrBlank()) {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid token"))
                        return@post
                    }

                    val request = call.receive<TokenRefreshRequest>()
                    val tokenResponse = authService.refreshToken(userId, request.refreshToken)
                    call.respond(HttpStatusCode.OK, tokenResponse)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to refresh token"))
                }
            }
        }
    }
}