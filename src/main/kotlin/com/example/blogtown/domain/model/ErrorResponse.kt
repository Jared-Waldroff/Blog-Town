package com.example.com.example.blogtown.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val error: String)