package org.example.expert.domain.user.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

class UserChangePasswordRequest(
    @field:NotBlank
    val oldPassword: String,

    @field:NotBlank
    @field:Size(min = 8, message = "새 비밀번호는 8자 이상이어야 합니다.")
    @field:Pattern(regexp = ".*\\d.*", message = "새 비밀번호는 숫자를 포함해야 합니다.")
    @field:Pattern(regexp = ".*[A-Z].*", message = "새 비밀번호는 대문자를 포함해야 합니다.")
    val newPassword: String
)
