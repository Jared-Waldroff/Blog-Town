package com.example.com.example.blogtown.web.routes
//
//import com.example.com.example.blogtown.domain.service.AuthService
//import io.ktor.http.*
//import io.ktor.server.application.*
//import io.ktor.server.auth.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*
//import io.ktor.server.sessions.*
//import org.koin.ktor.ext.inject
//import org.slf4j.LoggerFactory
//import java.util.*
//
//data class OAuthSession(
//    val state: String,
//    val redirectUrl: String? = null
//)
//
//fun Application.configureOAuthRoutes() {
//    val logger = LoggerFactory.getLogger("OAuthRoutes")
//    val authService by inject<AuthService>()
//
//    install(Sessions) {
//        cookie<OAuthSession>("oauth_session") {
//            cookie.path = "/"
//            cookie.maxAgeInSeconds = 600 // 10 minutes
//            cookie.secure = false // Set to true is production
//            cookie.httpOnly = true
//        }
//    }
//
//    fun generateState(): String {
//        return UUID.randomUUID().toString()
//    }
//
////    routing {
////        get("/oauth/login/google") {
////            val state = generateState()
////            val redirectUrl = call.request.queryParameters["redirect_url"]
////
////            // Store state and redirect
////            call.sessions.set(OAuthSession(state, redirectUrl))
////
////            // Redirect to google OAuth
////            call.respondRedirect("/oauth/authorize/google?state=$state")
////        }
////
////        get("/oauth/login/github") {
////            val state = generateState()
////            val redirectUrl = call.request.queryParameters["redirect_url"]
////
////            call.sessions.set(OAuthSession(state, redirectUrl))
////            call.respondRedirect("/oauth/authorize/github?state=$state")
////        }
////
////        get("/oauth/callback") {
////            val principal = call.principal<OAuthAccessTokenResponse>()
////            if (principal == null) {
////                call.respond(HttpStatusCode.Unauthorized, "OAuth authentication failed")
////                return@get
////            }
////
////            val session = call.sessions.get<OAuthSession>()
////            if (session == null) {
////                call.respond(HttpStatusCode.BadRequest, "Invalid session")
////            }
////
////            try {
////                val tokenResponse = authService.handleOAuthLogin(principal)
////                val redirectUrl = session?.redirectUrl ?: "/"
////
////                // Clear session after use
////                call.sessions.clear<OAuthSession>()
////
////                // Redirect with token
////                call.respondRedirect("$redirectUrl?token=${tokenResponse.accessToken}")
////            } catch (e: Exception) {
////                logger.error("OAuth callback error", e)
////                call.respond(HttpStatusCode.InternalServerError, "Authentication failed: ${e.message}")
////            }
////        }
//    }
//}