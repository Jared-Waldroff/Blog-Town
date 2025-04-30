package com.example.com.example.blogtown.services

import com.example.com.example.blogtown.domain.model.User
import com.example.com.example.blogtown.domain.repository.SavedBlogRepository
import com.example.com.example.blogtown.domain.repository.UserRepository
import com.example.com.example.blogtown.domain.service.UserService
import javax.print.attribute.standard.RequestingUserName

class UserServiceImpl (
    private val userRepository: UserRepository,
    ) : UserService {

    override suspend fun getUserByEmail(email: String): User? {
        return userRepository.getUserByEmail(email)
    }

    override suspend fun getUserById(id: String): User? {
        return userRepository.getUserById(id)
    }

    override suspend fun getUserByUsername(userName: String): User? {
        return userRepository.getUserByUsername(userName)
    }
}