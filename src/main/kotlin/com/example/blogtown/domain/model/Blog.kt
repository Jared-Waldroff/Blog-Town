package com.example.com.example.blogtown.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Blog(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val body: String,
    val tags: List<String>,
    val author: String,
    val dateCreated: Long = System.currentTimeMillis()
)

@Serializable
data class BlogCreateRequest(
    val title: String,
    val body: String,
    val tags: List<String>
)

@Serializable
data class BlogSearchRequest(
    val title: String? = null,
    val tags: List<String>? = null,
    val author: String? = null,
    val keywords: String? = null
)

@Serializable
data class BlogSearchResponse(
    val id: String,
    val title: String,
    val description: String,
    val author: String,
    val tags: List<String> = emptyList(),
    val dateCreated: Long
)

@Serializable
data class BlogSearchResponseList(
    val blogs: List<BlogSearchResponse>
)
