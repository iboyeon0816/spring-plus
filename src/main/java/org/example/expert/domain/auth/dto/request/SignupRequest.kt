package org.example.expert.domain.auth.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

class SignupRequest(
    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    val password: String,

    @field:NotBlank
    val userRole: String,

    @field:NotBlank
    val nickname: String
)