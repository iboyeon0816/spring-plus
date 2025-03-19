package org.example.expert.domain.user.repository

import org.example.expert.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun findByNickname(nickname: String): List<User>
    fun existsByEmail(email: String): Boolean
}
