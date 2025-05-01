package com.example.com.example.blogtown.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.config.*
import java.util.*

class JwtConfig(config: ApplicationConfig) {
    private val secret: String = config.property("jwt.secret").getString()
    private val issuer: String = config.property("jwt.issuer").getString()
    private val audience: String = config.property("jwt.audience").getString()

    val accessTokenExpiration: Long = config.property("jwt.accessTokenExpiration").getString().toLong()
    private val refreshTokenExpiration: Long = config.property("jwt.refreshTokenExpiration").getString().toLong()

    val realm: String = config.property("jwt.realm").getString()

    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .withAudience(audience)
        .build()

    fun generateAccessToken(userId: String): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withAudience(audience)
        .withClaim("userId", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + accessTokenExpiration))
        .sign(algorithm)

    fun generateRefreshToken(userId: String): String = JWT.create()
        .withSubject("Refresh")
        .withIssuer(issuer)
        .withAudience(audience)
        .withClaim("userId", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenExpiration))
        .sign(algorithm)
}