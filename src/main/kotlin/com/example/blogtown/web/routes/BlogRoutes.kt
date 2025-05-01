package com.example.com.example.blogtown.web.routes

import com.example.com.example.blogtown.domain.model.*
import com.example.com.example.blogtown.domain.service.BlogService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory

fun Application.configureBlogRoutes() {
    val logger = LoggerFactory.getLogger("BlogRoutes")
    val blogService by inject<BlogService>()

    routing {
        // Public search endpoint
        get("/blogs/search") {
            try {
                logger.info("Received blog search request")

                // Get search params from query
                val searchRequest = BlogSearchRequest(
                    title = call.request.queryParameters["title"],
                    tags = call.request.queryParameters["tags"]?.split(","),
                    author = call.request.queryParameters["author"],
                    keywords = call.request.queryParameters["keywords"]
                )

                val searchResults = blogService.searchBlogs(searchRequest)
                call.respond(mapOf("blogs" to searchResults))
            } catch (e: Exception) {
                logger.error("Error searching blogs", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to search blogs: ${e.message}"))
            }
        }

        // Protected endpoints requiring authentication
        authenticate("auth-jwt") {
            // Create a new blog post
            post("/blogs") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asString()
                    if (userId.isNullOrBlank()) {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid token"))
                        return@post
                    }

                    val request = call.receive<BlogCreateRequest>()
                    val blog = blogService.createBlogPost(userId, request)
                    call.respond(HttpStatusCode.Created, blog)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                } catch (e: Exception) {
                    logger.error("Error creating blog post", e)
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to create blog post: ${e.message}"))
                }
            }

            // Save (bookmark) a blog post
            post("/blogs/{blogId}/save") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asString()
                    if (userId.isNullOrBlank()) {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid token"))
                        return@post
                    }

                    val blogId = call.parameters["blogId"] ?: throw IllegalArgumentException("Blog ID is required")
                    val savedBlog = blogService.saveBlog(userId, blogId)
                    call.respond(HttpStatusCode.Created, savedBlog)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                } catch (e: Exception) {
                    logger.error("Error saving blog post", e)
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to save blog post: ${e.message}"))
                }
            }

            // Get saved blogs for a user
            get("/users/{userId}/saved") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val currentUserId = principal?.payload?.getClaim("userId")?.asString()
                    if (currentUserId.isNullOrBlank()) {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid token"))
                        return@get
                    }

                    val userId = call.parameters["userId"] ?: throw IllegalArgumentException("User ID is required")

                    // Optional pagination parameters
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10

                    val (blogs, totalPages) = blogService.getSavedBlogs(userId, page, size)
                    call.respond(HttpStatusCode.OK, mapOf(
                        "blogs" to blogs,
                        "page" to page,
                        "size" to size,
                        "totalPages" to totalPages
                    ))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                } catch (e: Exception) {
                    logger.error("Error retrieving saved blogs", e)
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to retrieve saved blogs: ${e.message}"))
                }
            }
        }
    }
}