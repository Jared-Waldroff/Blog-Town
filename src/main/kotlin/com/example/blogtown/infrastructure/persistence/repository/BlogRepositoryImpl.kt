package com.example.com.example.blogtown.infrastructure.persistence.repository

import com.example.com.example.blogtown.domain.model.Blog
import com.example.com.example.blogtown.domain.repository.BlogRepository

class BlogRepositoryImpl : BlogRepository {
    private val blogs = mutableMapOf<String, Blog>()

    override suspend fun createBlog(blog: Blog): Blog {
        blogs[blog.id] = blog
        return blog
    }

    override suspend fun getBlogById(id: String): Blog? {
        return blogs[id]
    }

    override suspend fun searchBlogs(keyword: String?, tag: String?, page: Int, size: Int): Pair<List<Blog>, Int> {
        var filteredBlogs = blogs.values.toList()

        // Apply keyword filter
        if (!keyword.isNullOrBlank()) {
            filteredBlogs = filteredBlogs.filter {
                it.title.contains(keyword, ignoreCase = true)
                it.body.contains(keyword, ignoreCase = true)
            }
        }

        // Apply tag filter
        if (!tag.isNullOrBlank()) {
            filteredBlogs = filteredBlogs.filter {
                it.tags.any { t -> t.equals(tag, ignoreCase = true)}
            }
        }

        // Sort blogs in descending order by date created
        val sortedBlogs = filteredBlogs.sortedByDescending { it.dateCreated }

        val totalCount = sortedBlogs.size
        val totalPages = (totalCount + size - 1) / size

        sortedBlogs
            .drop(page * size)
            .take(size)

        return Pair(sortedBlogs, totalPages)
    }
}