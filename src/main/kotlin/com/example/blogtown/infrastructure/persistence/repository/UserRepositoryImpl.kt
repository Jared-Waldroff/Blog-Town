package com.example.com.example.blogtown.infrastructure.persistence.repository

import com.example.com.example.blogtown.domain.model.User
import com.example.com.example.blogtown.domain.repository.UserRepository

class UserRepositoryImpl : UserRepository {
    private val users = mutableMapOf<String, User>()
    private val usersByEmail = mutableMapOf<String, User>()
    private val usersByUsername = mutableMapOf<String, User>()
    private val usersByOAuth = mutableMapOf<Pair<String, String>, User>()
    private val refreshTokens = mutableMapOf<String, String>()


    override suspend fun createUser(user: User): User {
        users[user.id] = user
        usersByEmail[user.email] = user
        usersByUsername[user.username] = user
        return user
    }

    override suspend fun getUserByEmail(email: String): User? {
        return usersByEmail[email]
    }

    override suspend fun getUserById(id: String): User? {
        return users[id]
    }

    override suspend fun getUserByUsername(username: String): User? {
        return usersByUsername[username]
    }

    override suspend fun storeRefreshToken(userId: String, token: String) {
        refreshTokens[userId] = token
    }

    override suspend fun invalidateRefreshToken(userId: String) {
        refreshTokens.remove(userId)
    }

    override suspend fun validateRefreshToken(userId: String, token: String): Boolean {
        return refreshTokens[userId] == token
    }

}