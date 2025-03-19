package org.example.expert.domain.todo.repository

import org.example.expert.domain.todo.dto.request.TodoSearchCondition
import org.example.expert.domain.todo.dto.response.TodoSearchResponse
import org.example.expert.domain.todo.entity.Todo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface TodoCustomRepository {
    fun findByIdWithUser(id: Long): Todo?
    fun findAllOrderByCreatedAtDesc(condition: TodoSearchCondition, pageable: Pageable): Page<TodoSearchResponse>
}
