package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.request.TodoSearchCondition;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TodoCustomRepository {
    Optional<Todo> findByIdWithUser(Long id);
    Page<TodoSearchResponse> findAllOrderByCreatedAtDesc(TodoSearchCondition condition, Pageable pageable);
}
