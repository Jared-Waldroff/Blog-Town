package com.example.com.example.blogtown.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class User(
    val id: String = UUID.randomUUID().toString(),
    val email: String,
    val username: String,
    val password: String,
    val timeCreated: Long = System.currentTimeMillis()
)

@Serializable
data class UserCreationRequest(
    val email: String,
    val username: String,
    val password: String
)

@Serializable
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)

@Serializable
data class LoginRequest(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class TokenRefreshRequest(
    val refreshToken: String
)