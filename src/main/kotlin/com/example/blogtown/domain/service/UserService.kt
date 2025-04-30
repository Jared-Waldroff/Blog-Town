package com.example.com.example.blogtown.domain.service

import com.example.com.example.blogtown.domain.model.User
import javax.print.attribute.standard.RequestingUserName

interface UserService {
    suspend fun getUserById(id: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun getUserByUsername(userName: String): User?
}