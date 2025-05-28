package com.example.com.example.blogtown.domain.repository

import com.example.com.example.blogtown.domain.model.Blog

interface BlogRepository {
    suspend fun createBlog(blog: Blog): Blog
    suspend fun getBlogById(id: String): Blog?
    suspend fun searchBlogs(keyword: String?, tag: String?, page: Int, size: Int): Pair<List<Blog>, Int>
    suspend fun updateBlog(blogId: String, userId: String, blog: Blog): Blog?
}