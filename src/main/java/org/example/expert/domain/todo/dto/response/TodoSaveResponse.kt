package org.example.expert.domain.todo.dto.response

import org.example.expert.domain.user.dto.response.UserResponse

class TodoSaveResponse(
    val id: Long,
    val title: String,
    val contents: String,
    val weather: String,
    val user: UserResponse
)
