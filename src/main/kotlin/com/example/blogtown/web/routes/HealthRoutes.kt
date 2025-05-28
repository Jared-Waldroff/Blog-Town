package com.example.com.example.blogtown.web.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

fun Application.configureHealthRoutes() {
    val logger = LoggerFactory.getLogger("HealthRoutes")

    routing {
        // Kubernetes liveness probe - checks if app is running
        get("/health/live") {
            logger.debug("Liveness check requested")
            call.respond(HttpStatusCode.OK, mapOf(
                "status" to "alive",
                "timestamp" to System.currentTimeMillis(),
                "service" to "blog-town-api"
            ))
        }

        // Kubernetes readiness probe - checks if app is ready to serve traffic
        get("/health/ready") {
            try {
                // Add any readiness checks here (DB connections, etc.)
                // For now, just check if the application is initialized
                logger.debug("Readiness check requested")

                call.respond(HttpStatusCode.OK, mapOf(
                    "status" to "ready",
                    "timestamp" to System.currentTimeMillis(),
                    "service" to "blog-town-api",
                    "checks" to mapOf(
                        "application" to "initialized",
                        "dependencies" to "available"
                    )
                ))
            } catch (e: Exception) {
                logger.error("Readiness check failed", e)
                call.respond(HttpStatusCode.ServiceUnavailable, mapOf(
                    "status" to "not_ready",
                    "error" to e.message,
                    "timestamp" to System.currentTimeMillis()
                ))
            }
        }

        // General health endpoint
        get("/health") {
            logger.debug("General health check requested")
            call.respond(HttpStatusCode.OK, mapOf(
                "status" to "healthy",
                "timestamp" to System.currentTimeMillis(),
                "service" to "blog-town-api",
                "version" to "1.0.0",
                "uptime" to System.currentTimeMillis()
            ))
        }

        // Startup probe - checks if app has started successfully
        get("/health/startup") {
            logger.debug("Startup check requested")
            call.respond(HttpStatusCode.OK, mapOf(
                "status" to "started",
                "timestamp" to System.currentTimeMillis(),
                "service" to "blog-town-api"
            ))
        }
    }
}