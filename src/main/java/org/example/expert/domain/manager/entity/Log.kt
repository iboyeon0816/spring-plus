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
    var id: Long? = null
        protected set

    @Column(nullable = false)
    var requesterId: Long = requesterId
        protected set

    @Column(nullable = false)
    var assigneeId: Long = assigneeId
        protected set

    @Column(nullable = false)
    var todoId: Long = todoId
        protected set

    @CreatedDate
    @Column(updatable = false)
    var createdAt: LocalDateTime? = null
        protected set
}
