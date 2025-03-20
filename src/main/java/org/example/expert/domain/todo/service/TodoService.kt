package org.example.expert.domain.todo.service

import org.example.expert.client.WeatherClient
import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.todo.dto.request.TodoSaveRequest
import org.example.expert.domain.todo.dto.request.TodoSearchCondition
import org.example.expert.domain.todo.dto.response.TodoResponse
import org.example.expert.domain.todo.dto.response.TodoSaveResponse
import org.example.expert.domain.todo.dto.response.TodoSearchResponse
import org.example.expert.domain.todo.entity.Todo
import org.example.expert.domain.todo.repository.TodoRepository
import org.example.expert.domain.user.dto.response.UserResponse
import org.example.expert.domain.user.entity.User.Companion.fromAuthUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class TodoService(
    private val todoRepository: TodoRepository,
    private val weatherClient: WeatherClient
) {
    @Transactional
    fun saveTodo(authUser: AuthUser, todoSaveRequest: TodoSaveRequest): TodoSaveResponse {
        val user = fromAuthUser(authUser)
        val weather = weatherClient.todayWeather
        val todo = Todo(todoSaveRequest.title, todoSaveRequest.contents, weather, user)

        todoRepository.save(todo)

        val userResponse = UserResponse(user.id, user.email, user.nickname)
        return TodoSaveResponse(todo.id, todo.title, todo.contents, weather, userResponse)
    }

    @Transactional(readOnly = true)
    fun getTodos(
        page: Int, size: Int, weather: String?, modifiedFrom: LocalDate?, modifiedTo: LocalDate?
    ): Page<TodoResponse> {
        val pageable = PageRequest.of(page - 1, size)
        val todos = todoRepository.findByWeatherAndModifiedAtBetween(weather, modifiedFrom, modifiedTo, pageable)

        return todos.map {
            with(it) {
                val userResponse = UserResponse(user.id, user.email, user.nickname)
                TodoResponse(id, title, contents, it.weather, userResponse, createdAt, modifiedAt)
            }
        }
    }

    @Transactional(readOnly = true)
    fun searchTodos(page: Int, size: Int, condition: TodoSearchCondition): Page<TodoSearchResponse> {
        val pageable = PageRequest.of(page - 1, size)
        return todoRepository.findAllOrderByCreatedAtDesc(condition, pageable)
    }

    @Transactional(readOnly = true)
    fun getTodo(todoId: Long): TodoResponse {
        val todo = todoRepository.findByIdWithUser(todoId) ?: throw InvalidRequestException("Todo not found")

        return with(todo) {
            val userResponse = UserResponse(user.id, user.email, user.nickname)
            TodoResponse(id, title, contents, weather, userResponse, createdAt, modifiedAt)
        }
    }
}
