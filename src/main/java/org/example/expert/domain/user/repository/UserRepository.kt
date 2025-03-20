package org.example.expert.domain.user.repository

import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull

fun UserRepository.findByIdOrElseThrow(userId: Long) = this.findByIdOrNull(userId)
    ?: throw InvalidRequestException("User not found")

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun findByNickname(nickname: String): List<User>
    fun existsByEmail(email: String): Boolean
}
