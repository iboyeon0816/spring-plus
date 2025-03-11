package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;

import java.util.Optional;

public interface TodoCustomRepository {
    Optional<Todo> findByIdWithUser(Long id);
}
