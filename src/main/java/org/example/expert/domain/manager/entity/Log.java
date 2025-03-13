package org.example.expert.domain.manager.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 매니저 등록 요청을 기록하는 로그 테이블
 */
@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Log {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long requesterId; // 매니저 등록을 요청한 사람 id (일정의 작성자)

    @Column(nullable = false)
    private Long assigneeId; // 매니저 등록 요청을 받은 사람 id

    @Column(nullable = false)
    private Long todoId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Log(Long requesterId, Long assigneeId, Long todoId) {
        this.requesterId = requesterId;
        this.assigneeId = assigneeId;
        this.todoId = todoId;
    }
}
