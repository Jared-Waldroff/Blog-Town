package com.example.com.example.blogtown.domain.service

import com.example.com.example.blogtown.domain.model.User

interface UserService {
    suspend fun getUserById(id: String): User?
    suspend fun getUserByEmail(email: String): User?
}