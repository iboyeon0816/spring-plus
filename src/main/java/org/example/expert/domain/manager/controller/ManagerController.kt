package org.example.expert.domain.manager.controller

import jakarta.validation.Valid
import org.example.expert.domain.common.annotation.Auth
import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest
import org.example.expert.domain.manager.dto.response.ManagerResponse
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse
import org.example.expert.domain.manager.service.ManagerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class ManagerController(
    private val managerService: ManagerService
) {
    @PostMapping("/todos/{todoId}/managers")
    fun saveManager(
        @Auth authUser: AuthUser,
        @PathVariable todoId: Long,
        @RequestBody @Valid managerSaveRequest: ManagerSaveRequest
    ): ResponseEntity<ManagerSaveResponse> {
        return ResponseEntity.ok(managerService.saveManager(authUser, todoId, managerSaveRequest))
    }

    @GetMapping("/todos/{todoId}/managers")
    fun getMembers(@PathVariable todoId: Long): ResponseEntity<List<ManagerResponse>> {
        return ResponseEntity.ok(managerService.getManagers(todoId))
    }

    @DeleteMapping("/todos/{todoId}/managers/{managerId}")
    fun deleteManager(
        @Auth authUser: AuthUser,
        @PathVariable todoId: Long,
        @PathVariable managerId: Long
    ) {
        managerService.deleteManager(authUser, todoId, managerId)
    }
}
