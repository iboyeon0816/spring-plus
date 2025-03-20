package org.example.expert.domain.manager.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * 매니저 등록 요청을 기록하는 로그 테이블
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
class Log(
    requesterId: Long, assigneeId: Long, todoId: Long
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column(nullable = false)
    val requesterId: Long = requesterId

    @Column(nullable = false)
    val assigneeId: Long = assigneeId

    @Column(nullable = false)
    val todoId: Long = todoId

    @CreatedDate
    @Column(updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
        protected set
}
