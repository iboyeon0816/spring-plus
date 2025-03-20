package org.example.expert.domain.todo.controller

import jakarta.validation.Valid
import org.example.expert.domain.common.annotation.Auth
import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.todo.dto.request.TodoSaveRequest
import org.example.expert.domain.todo.dto.request.TodoSearchCondition
import org.example.expert.domain.todo.dto.response.TodoResponse
import org.example.expert.domain.todo.dto.response.TodoSaveResponse
import org.example.expert.domain.todo.dto.response.TodoSearchResponse
import org.example.expert.domain.todo.service.TodoService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
class TodoController(
    private val todoService: TodoService
) {
    @PostMapping("/todos")
    fun saveTodo(
        @Auth authUser: AuthUser,
        @RequestBody @Valid todoSaveRequest: TodoSaveRequest
    ): ResponseEntity<TodoSaveResponse> {
        return ResponseEntity.ok(todoService.saveTodo(authUser, todoSaveRequest))
    }

    @GetMapping("/todos")
    fun getTodos(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) weather: String?,
        @RequestParam(required = false) modifiedFrom: LocalDate?,
        @RequestParam(required = false) modifiedTo: LocalDate?
    ): ResponseEntity<Page<TodoResponse>> {
        return ResponseEntity.ok(todoService.getTodos(page, size, weather, modifiedFrom, modifiedTo))
    }

    @GetMapping("/todos/search")
    fun searchTodos(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @ModelAttribute request: TodoSearchCondition
    ): ResponseEntity<Page<TodoSearchResponse>> {
        return ResponseEntity.ok(todoService.searchTodos(page, size, request))
    }

    @GetMapping("/todos/{todoId}")
    fun getTodo(@PathVariable todoId: Long): ResponseEntity<TodoResponse> {
        return ResponseEntity.ok(todoService.getTodo(todoId))
    }
}
