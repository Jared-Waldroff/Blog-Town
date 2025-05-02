package com.example.com.example.blogtown.services

import com.example.com.example.blogtown.config.JwtConfig
import com.example.com.example.blogtown.domain.model.LoginRequest
import com.example.com.example.blogtown.domain.model.TokenResponse
import com.example.com.example.blogtown.domain.model.User
import com.example.com.example.blogtown.domain.model.UserCreationRequest
import com.example.com.example.blogtown.domain.repository.UserRepository
import com.example.com.example.blogtown.domain.service.AuthService
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.LoggerFactory
import java.util.*

class AuthServiceImpl(private val userRepository: UserRepository) : AuthService, KoinComponent {
    private val jwtConfig: JwtConfig by inject()
    private val logger = LoggerFactory.getLogger((AuthServiceImpl::class.java))

    override suspend fun createUser(request: UserCreationRequest): User {
        userRepository.getUserByEmail(request.email)?.let {
            throw IllegalArgumentException("User with email ${request.email} aldready exists")
        }

        // Hash password
        val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())

        val user = User(
            email = request.email,
            username = request.username,
            password = hashedPassword
        )

        return userRepository.createUser(user)
    }

    override suspend fun loginUser(request: LoginRequest): TokenResponse {
        println("Login attempt with username: ${request.username}, email: ${request.email}")

        // Find user by email or username
        val userByEmail = if (request.email.isNotBlank()) userRepository.getUserByEmail(request.email) else null
        val userByUsername = if (request.username.isNotBlank()) userRepository.getUserByUsername(request.username) else null
        val user = userByEmail ?: userByUsername ?: throw IllegalArgumentException("Invalid username or password")

        // Verify Password with encryption
        if (!BCrypt.checkpw(request.password, user.password)) {
            throw IllegalArgumentException("Invalid username or password")
        }

        // Generate access and refresh tokens
        val accessToken = jwtConfig.generateAccessToken(user.id)
        val refreshToken = jwtConfig.generateRefreshToken(user.id)

        // Store refresh token
        userRepository.storeRefreshToken(user.id, refreshToken)

        return TokenResponse(
            accessToken,
            refreshToken,
            expiresIn = jwtConfig.accessTokenExpiration)
    }

    override suspend fun validateToken(credential: JWTCredential): JWTPrincipal? {
        val userId = credential.payload.getClaim("userId").asString()
        return if (userId.isNotBlank()) {
            JWTPrincipal(credential.payload)
        } else {
            null
        }
    }

    override suspend fun refreshToken(userId: String, refreshToken: String): TokenResponse {
        // Validate refresh token
        if (!userRepository.validateRefreshToken(userId, refreshToken)) {
            throw IllegalArgumentException("Not a valid refresh token")
        }

        // Generate new tokens
        val newAccessToken = jwtConfig.generateAccessToken(userId)
        val newRefreshToken = jwtConfig.generateRefreshToken(userId)

        // Store new tokens
        userRepository.invalidateRefreshToken(userId)
        userRepository.storeRefreshToken(userId, newRefreshToken)

        return TokenResponse(
            newAccessToken,
            newRefreshToken,
            expiresIn = jwtConfig.accessTokenExpiration)
    }

//    override suspend fun handleOAuthLogin(principal: OAuthAccessTokenResponse): TokenResponse {
//        logger.info("Processing OAuth Login")
//
//        // Extract provider from principal
//        val provider = when (principal) {
//            is OAuthAccessTokenResponse.OAuth2 ->
//                principal.extraParameters["provider"] ?: throw IllegalArgumentException("Unknown OAuth provider")
//            else -> throw IllegalArgumentException("Unsupported OAuth response type")
//        }
//
//        // Get user info from provider using access token
//        val userInfo = when (provider) {
//            "google" -> getUserInfoFromGoogle(principal.accessToken)
//            "github" -> getUserInfoFromGithub(principal.accessToken)
//            else -> throw IllegalArgumentException("Unsupported Provider: $provider")
//        }
//
//        // Create or update user
//        val user = createOrUpdateOAuthUser(
//            provider = provider,
//            id = userInfo.first,
//            email = userInfo.second,
//            displayName = userInfo.third
//        )
//
//        // Generate JWT tokens
//        val accessToken = jwtConfig.generateAccessToken(user.id)
//        val refreshToken = jwtConfig.generateRefreshToken(user.id)
//
//        // Store refresh tokens
//        userRepository.storeRefreshToken(user.id, refreshToken)
//
//        return TokenResponse(
//            accessToken = accessToken,
//            refreshToken = refreshToken,
//            expiresIn = jwtConfig.accessTokenExpiration
//        )
//
//    }

//    override suspend fun createOrUpdateOAuthUser(
//        provider: String,
//        id: String,
//        email: String,
//        displayName: String
//    ): User {
//        val existingUser = userRepository.getUserByOAuth(provider, id)
//
//        if (existingUser != null) {
//            return existingUser
//        }
//
//        // Create new user
//        val randomPassword = UUID.randomUUID().toString()
//        val username = generateUniqueUsername(displayName)
//        val newUser = User(
//            email = email,
//            username = username,
//            password = randomPassword,
//            oauthProvider = provider,
//            oauthId = id
//        )
//
//        return userRepository.createUser(newUser)
//    }

    // Helper methods for OAuth providers
//    private suspend fun getUserInfoFromGoogle(accessToken: String): Triple<String, String, String> {
//        val userInfo = httpClient.get("https://www.googleapis.com/oauth2/v2/userinfo"){
//            headers {
//                append(HttpHeaders.Authorization, "Bearer $accessToken")
//            }
//        }.body<JsonObject>()
//
//        val id = userInfo["id"]?.jsonPrimitive?.content ?: ""
//        val email = userInfo["email"]?.jsonPrimitive?.content ?: ""
//        val name = userInfo["name"]?.jsonPrimitive?.content ?: ""
//
//        return Triple(id, email, name)
//    }
//
//    private suspend fun getUserInfoFromGithub(accessToken: String): Triple<String, String, String> {
//        val userInfo = httpClient.get("https://api.github.com/user") {
//            headers {
//                append(HttpHeaders.Authorization, "Bearer $accessToken")
//                append(HttpHeaders.Accept, "application/json")
//            }
//        }.body<JsonObject>()
//
//        val id = userInfo["id"]?.jsonPrimitive?.content ?: ""
//        val email = userInfo["email"]?.jsonPrimitive?.content ?: ""
//        val name = userInfo["name"]?.jsonPrimitive?.content
//            ?: userInfo["login"]?.jsonPrimitive?.content
//            ?: ""
//
//        return Triple(id, email, name)
//    }

    // Generate a unique username
//    private suspend fun generateUniqueUsername(name: String): String {
//        val baseUserName = name.lowercase()
//            .replace(Regex("[^a-z0-9]"), "")
//            .take(20)
//
//        var username = baseUserName
//        var counter = 1
//
//        while (userRepository.getUserByUsername(username) != null) {
//            username = "$baseUserName$counter"
//            counter++
//        }
//
//        return username
//    }
}