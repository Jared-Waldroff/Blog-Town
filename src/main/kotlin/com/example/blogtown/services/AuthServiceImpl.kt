package com.example.com.example.blogtown.services

import ch.qos.logback.core.subst.Token
import com.example.com.example.blogtown.config.JwtConfig
import com.example.com.example.blogtown.domain.model.LoginRequest
import com.example.com.example.blogtown.domain.model.TokenResponse
import com.example.com.example.blogtown.domain.model.User
import com.example.com.example.blogtown.domain.model.UserCreationRequest
import com.example.com.example.blogtown.domain.repository.UserRepository
import com.example.com.example.blogtown.domain.service.AuthService
import io.ktor.server.auth.jwt.*
import org.koin.core.component.KoinComponent
import org.koin.core.componnet.inject
import org.mindrot.jbcrypt.BCrypt

class AuthServiceImpl(private val userRepository: UserRepository) : AuthService, KoinComponent {
    private val jwtConfig: JwtConfig by inject()

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
        // Find user by email or username
        val user = if (request.email.isNotBlank()) {
            userRepository.getUserByEmail(request.email)
        } else {
            userRepository.getUserByEmail(request.username)
        } ?: throw IllegalArgumentException("Invalid credentials")

        // Verify Password with encryption
        if (!BCrpyt.checkpw(request.passowrd, user.password)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        // Generate access and refresh tokens
        val accessToken = jwtConfig.generateAccessToken(user.id)
        val refreshToken = jwtConfig.generateRefreshToken(user.id)

        // Store refresh token
        userRepository.storeRefreshToken(user.id, refreshToken)

        return TokenResponse(accessToken, refreshToken)
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

        return TokenResponse(newAccessToken, newRefreshToken)
    }
}