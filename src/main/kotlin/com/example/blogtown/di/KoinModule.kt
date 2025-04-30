package com.example.com.example.blogtown.di


import com.example.com.example.blogtown.domain.repository.BlogRepository
import com.example.com.example.blogtown.domain.repository.SavedBlogRepository
import com.example.com.example.blogtown.domain.repository.UserRepository
import com.example.com.example.blogtown.domain.service.AuthService
import com.example.com.example.blogtown.domain.service.BlogService
import com.example.com.example.blogtown.domain.service.UserService
import com.example.com.example.blogtown.infrastructure.persistence.repository.BlogRepositoryImpl
import com.example.com.example.blogtown.infrastructure.persistence.repository.SavedBlogRepositoryImpl
import com.example.com.example.blogtown.infrastructure.persistence.repository.UserRepositoryImpl
import com.example.com.example.blogtown.services.AuthServiceImpl
import com.example.com.example.blogtown.services.BlogServiceImpl
import com.example.com.example.blogtown.services.UserServiceImpl
import org.koin.dsl.module

val appModule = module {
    // Repositories
    single<UserRepository> { UserRepositoryImpl() }
    single<BlogRepository> { BlogRepositoryImpl() }
    single<SavedBlogRepository> { SavedBlogRepositoryImpl() }

    // Services
    single<AuthService> { AuthServiceImpl(get()) }
    single<UserService> { UserServiceImpl(get()) }
    single<BlogService> { BlogServiceImpl(get(), get(), get()) }
}