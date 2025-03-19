package org.example.expert.domain.manager.repository

import org.example.expert.domain.manager.entity.Log
import org.springframework.data.jpa.repository.JpaRepository

interface LogRepository : JpaRepository<Log, Long>
