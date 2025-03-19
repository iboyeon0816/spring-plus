package org.example.expert.domain.common.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class Timestamped {
    @CreatedDate
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var createdAt: LocalDateTime? = null
        protected set

    @LastModifiedDate
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    var modifiedAt: LocalDateTime? = null
        protected set
}
