package com.example.com.example.blogtown.di


import com.example.com.example.blogtown.config.JwtConfig
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
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.dsl.module
import org.slf4j.LoggerFactory

fun createConfigModule(application: Application) = module {
    single { JwtConfig(application.environment.config) }
}

val appModule = module {
    single { LoggerFactory.getLogger("AppLogger") }}

val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    single<BlogRepository> { BlogRepositoryImpl() }
    single<SavedBlogRepository> { SavedBlogRepositoryImpl() }
}

val serviceModule = module {
    single<AuthService> { AuthServiceImpl(get()) }
    single<UserService> { UserServiceImpl(get()) }
    single<BlogService> { BlogServiceImpl(get(), get(), get()) }
}
