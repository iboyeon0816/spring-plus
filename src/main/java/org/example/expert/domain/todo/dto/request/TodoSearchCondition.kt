package org.example.expert.domain.todo.dto.request

import java.time.LocalDate

class TodoSearchCondition(
    val title: String? = null,
    val createdFrom: LocalDate? = null,
    val createdTo: LocalDate? = null,
    val managerNickname: String? = null
)
