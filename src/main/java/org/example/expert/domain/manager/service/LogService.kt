package org.example.expert.domain.manager.service

import org.example.expert.domain.manager.entity.Log
import org.example.expert.domain.manager.repository.LogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class LogService(
    private val logRepository: LogRepository
) {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun save(userId: Long, managerUserId: Long, todoId: Long) {
        val managerLog = Log(userId, managerUserId, todoId)
        logRepository.save(managerLog)
    }
}
