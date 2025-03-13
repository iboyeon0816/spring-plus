package org.example.expert.domain.manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.manager.entity.Log;
import org.example.expert.domain.manager.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(Long userId, Long managerUserId, Long todoId) {
        Log managerLog = new Log(userId, managerUserId, todoId);
        try {
            logRepository.save(managerLog);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
