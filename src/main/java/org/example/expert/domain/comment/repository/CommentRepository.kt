package org.example.expert.domain.comment.repository

import org.example.expert.domain.comment.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CommentRepository : JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.todo.id = :todoId")
    fun findByTodoIdWithUser(@Param("todoId") todoId: Long): List<Comment>
}
