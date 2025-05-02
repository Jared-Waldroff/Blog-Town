package com.example.com.example.blogtown.domain.repository

import com.example.com.example.blogtown.domain.model.User

interface UserRepository {
    suspend fun createUser(user: User): User
    suspend fun getUserByEmail(email: String): User?
    suspend fun getUserById(id: String): User?
    suspend fun storeRefreshToken(userId: String, token: String)
    suspend fun validateRefreshToken(userId: String, token: String): Boolean
    suspend fun invalidateRefreshToken(userId: String)
    suspend fun getUserByUsername(username: String): User?
}