package com.example.com.example.blogtown.domain.repository

import com.example.com.example.blogtown.domain.model.SavedBlog

interface SavedBlogRepository {
    suspend fun saveBlog(savedBlog: SavedBlog) : SavedBlog
    suspend fun getSavedBlogsByUserId(userId: String, page: Int,size: Int): Pair<List<SavedBlog>, Int>
    suspend fun isBlogSavedByUser(userId: String, blogId: String): Boolean

}