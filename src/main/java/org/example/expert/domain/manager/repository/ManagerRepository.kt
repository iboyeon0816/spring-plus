package org.example.expert.domain.manager.repository

import org.example.expert.domain.manager.entity.Manager
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ManagerRepository : JpaRepository<Manager, Long> {
    @Query("SELECT m FROM Manager m JOIN FETCH m.user WHERE m.todo.id = :todoId")
    fun findByTodoIdWithUser(@Param("todoId") todoId: Long): List<Manager>
}
