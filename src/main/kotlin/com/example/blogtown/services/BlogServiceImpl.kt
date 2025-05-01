package com.example.com.example.blogtown.services

import com.example.com.example.blogtown.domain.model.*
import com.example.com.example.blogtown.domain.repository.BlogRepository
import com.example.com.example.blogtown.domain.repository.SavedBlogRepository
import com.example.com.example.blogtown.domain.repository.UserRepository
import com.example.com.example.blogtown.domain.service.BlogService
import org.slf4j.LoggerFactory

class BlogServiceImpl(
    private val blogRepository: BlogRepository,
    private val savedBlogRepository: SavedBlogRepository,
    private val userRepository: UserRepository
) : BlogService {
    private val logger = LoggerFactory.getLogger(BlogServiceImpl::class.java)

    override suspend fun createBlogPost(userId: String, request: BlogCreateRequest): Blog {
        // Verify current user exists
        val user = userRepository.getUserById(userId) ?:
            throw IllegalArgumentException("User not found")

        // Use first 100 chars of blog as description
        val desciption = if (request.body.length > 100) {
            request.body.substring(0, 100) + "..."
        } else {
            request.body
        }

        val blog = Blog(
            title = request.title,
            body = request.body,
            description = desciption,
            tags = request.tags,
            author = userId
        )

        logger.info("Creating new blog post for user $userId: ${request.title}")
        return blogRepository.createBlog(blog)
    }

    override suspend fun searchBlogs(request: BlogSearchRequest): List<BlogSearchResponse> {
        logger.info("Searching blogs with criteria: $request")

        val keyword = request.keywords
        val tag = request.tags?.firstOrNull()

        val(blogs, _) = blogRepository.searchBlogs(keyword, tag, 0, 50)

        return blogs.map { blog ->
            BlogSearchResponse(
                title = blog.title,
                description = blog.description,
                author = blog.author,
                dateCreated = blog.dateCreated,
                id = blog.id
            )
        }
    }

    override suspend fun saveBlog(userId: String, blogId: String): SavedBlog {
        // Check blog exists
        val blog = blogRepository.getBlogById(blogId) ?:
            throw IllegalArgumentException("Blog not found")

        // Check user exists
        val user = userRepository.getUserById(userId) ?:
            throw IllegalArgumentException("User not found")

        // Check if already saved
        if (savedBlogRepository.isBlogSavedByUser(userId, blogId)) {
            throw IllegalArgumentException("Blog already saved by user")
        }

        // Create blog entity
        val savedBlog = SavedBlog(
            userId = userId,
            blogId = blogId
        )

        logger.info("User $userId saving blog $blogId")

        // Persist saved blog
        return savedBlogRepository.saveBlog(savedBlog)
    }

    override suspend fun getSavedBlogs(userId: String, page: Int, size: Int): Pair<List<Blog>, Int> {
        logger.info("Fetching saved blogs for user $userId (page: $page, size: $size)")

        // Get saved blog references
        val (savedBlogs, totalPages) = savedBlogRepository.getSavedBlogsByUserId(userId, page, size)

        // Fetch full blog object for each refernce
        val blogs = savedBlogs.mapNotNull { saved ->
            blogRepository.getBlogById(saved.blogId)
        }

        return Pair(blogs, totalPages)
    }

    override suspend fun getBlogById(blogId: String): Blog? {
        logger.info("Fetching blog with ID: $blogId")

        return blogRepository.getBlogById(blogId)
    }
}