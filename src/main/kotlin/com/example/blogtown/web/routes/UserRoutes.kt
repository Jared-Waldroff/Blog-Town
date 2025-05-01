package com.example.com.example.blogtown.web.routes

import com.example.com.example.blogtown.domain.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory

fun Application.configureUserRoutes() {
    val logger = LoggerFactory.getLogger("UserRoutes")
    val userService by inject<UserService>()

    routing {
        authenticate("auth-jwt") {
            // Get user profile
            get("/users/{userId}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val currentUserId = principal?.payload?.getClaim("userId")?.asString()
                    if (currentUserId.isNullOrBlank()) {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid token"))
                        return@get
                    }

                    val userId = call.parameters["userId"] ?: throw IllegalArgumentException("User ID is required")

                    // For security, only allow users to access their own profile
                    // or implement admin role check here
                    if (currentUserId != userId) {
                        call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Access denied"))
                        return@get
                    }

                    val user = userService.getUserById(userId)
                    if (user == null) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                        return@get
                    }

                    // Return user without sensitive data
                    call.respond(HttpStatusCode.OK, mapOf(
                        "id" to user.id,
                        "username" to user.username,
                        "email" to user.email,
                        "createdAt" to user.timeCreated
                    ))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                } catch (e: Exception) {
                    logger.error("Error retrieving user", e)
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to retrieve user: ${e.message}"))
                }
            }
        }
    }
}