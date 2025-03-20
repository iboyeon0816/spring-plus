package org.example.expert.domain.user.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

class UserRoleChangeRequest(
    @JsonProperty("role")
    val role: String
)
