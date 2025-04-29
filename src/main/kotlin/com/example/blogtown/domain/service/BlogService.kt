package com.example.com.example.blogtown.domain.service

import com.example.com.example.blogtown.domain.model.*

interface BlogService {
    suspend fun createBlogPost(userId: String, request: BlogCreateRequest): Blog
    suspend fun searchBlogs(request: BlogSearchRequest): List<BlogSearchResponse>
    suspend fun saveBlog(userId: String, blogId: String): SavedBlog
    suspend fun getSavedBlogs(userId: String, page: Int, size: Int): Pair<List<Blog>, Int>
}