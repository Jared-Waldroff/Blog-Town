package com.example.com.example.blogtown.persistence.repository

import com.example.com.example.blogtown.domain.model.Blog
import com.example.com.example.blogtown.domain.model.SavedBlog
import com.example.com.example.blogtown.domain.repository.SavedBlogRepository

class SavedBlogRepositoryImpl : SavedBlogRepository {
    private val savedBlogs = mutableMapOf<String, SavedBlog>()

    override suspend fun saveBlog(savedBlog: SavedBlog): SavedBlog {
        savedBlogs[savedBlog.id] = savedBlog
        return savedBlog
    }

    override suspend fun isBlogSavedByUser(userId: String, blogId: String): Boolean {
        return savedBlogs.values.any { it.userId == userId && it.blogId == blogId }
    }

    override suspend fun getSavedBlogsByUserId(userId: String, page: Int, size: Int): Pair<List<SavedBlog>, Int> {
        val userSavedBlogs = savedBlogs.values.filter { it.userId == userId }
            . sortedByDescending { it.savedAt }

        // Calculate pagination
        val totalCount = userSavedBlogs.size
        val totalPages = (totalCount + size - 1) / size

        val paginatedSavedBlogs = userSavedBlogs
            .drop(page * size)
            .take(size)

        return Pair(paginatedSavedBlogs, totalPages)
    }
}