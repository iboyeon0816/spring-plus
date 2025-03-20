package org.example.expert.domain.manager.repository

import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.manager.entity.Manager
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.findByIdOrNull
import org.springframework.data.repository.query.Param

fun ManagerRepository.findByIdOrElseThrow(managerId: Long) = this.findByIdOrNull(managerId)
    ?: throw InvalidRequestException("Manager not found")

interface ManagerRepository : JpaRepository<Manager, Long> {
    @Query("SELECT m FROM Manager m JOIN FETCH m.user WHERE m.todo.id = :todoId")
    fun findByTodoIdWithUser(@Param("todoId") todoId: Long): List<Manager>
}
