package org.example.expert.domain.manager.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

class ManagerSaveRequest (
    @JsonProperty("managerUserId")
    @field:NotNull
    val managerUserId: Long
)