package org.example.expert.domain.todo.repository

import org.example.expert.domain.todo.entity.Todo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface TodoRepository : JpaRepository<Todo, Long>, TodoCustomRepository {
    // 날씨와 수정일 범위에 맞는 할 일을 수정일 내림차순으로 조회
    @Query(
        "SELECT t " +
                "FROM Todo t " +
                "LEFT JOIN FETCH t.user u " +
                "WHERE (:weather IS NULL OR :weather = '' OR t.weather = :weather) " +
                "AND (:modifiedFrom IS NULL OR DATE(t.modifiedAt) >= :modifiedFrom) " +
                "AND (:modifiedTo IS NULL OR DATE(t.modifiedAt) <= :modifiedTo) " +
                "ORDER BY t.modifiedAt DESC"
    )
    fun findByWeatherAndModifiedAtBetween(
        @Param("weather") weather: String?,
        @Param("modifiedFrom") modifiedFrom: LocalDate?,
        @Param("modifiedTo") modifiedTo: LocalDate?,
        pageable: Pageable
    ): Page<Todo>
}
