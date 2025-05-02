package com.example.com.example.blogtown.domain.service

import com.auth0.jwt.JWT
import com.example.com.example.blogtown.domain.model.LoginRequest
import com.example.com.example.blogtown.domain.model.TokenResponse
import com.example.com.example.blogtown.domain.model.User
import com.example.com.example.blogtown.domain.model.UserCreationRequest
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.security.Principal

interface AuthService {
    suspend fun createUser(request: UserCreationRequest): User
    suspend fun loginUser(request: LoginRequest): TokenResponse
    suspend fun refreshToken(userId: String, refreshToken: String): TokenResponse
    suspend fun validateToken(credential: JWTCredential): JWTPrincipal?
}