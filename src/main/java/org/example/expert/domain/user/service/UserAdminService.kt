package org.example.expert.domain.user.service

import org.example.expert.domain.user.dto.request.UserRoleChangeRequest
import org.example.expert.domain.user.enums.UserRole
import org.example.expert.domain.user.repository.UserRepository
import org.example.expert.domain.user.repository.findByIdOrElseThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserAdminService(
    private val userRepository: UserRepository
) {
    @Transactional
    fun changeUserRole(userId: Long, userRoleChangeRequest: UserRoleChangeRequest) {
        val user = userRepository.findByIdOrElseThrow(userId)
        user.userRole = UserRole.of(userRoleChangeRequest.role)
    }
}
