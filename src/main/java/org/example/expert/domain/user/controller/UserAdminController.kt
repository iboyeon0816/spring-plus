package org.example.expert.domain.user.controller

import org.example.expert.domain.user.dto.request.UserRoleChangeRequest
import org.example.expert.domain.user.service.UserAdminService
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserAdminController(
    private val userAdminService: UserAdminService
) {
    @PatchMapping("/admin/users/{userId}")
    fun changeUserRole(@PathVariable userId: Long, @RequestBody userRoleChangeRequest: UserRoleChangeRequest) {
        userAdminService.changeUserRole(userId, userRoleChangeRequest)
    }
}
