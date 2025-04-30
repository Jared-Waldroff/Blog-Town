package com.example.com.example.blogtown.domain.model

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class SavedBlog(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val blogId: String,
    val savedAt: Long = System.currentTimeMillis()
)
