package org.example.expert.domain.user.controller

import jakarta.validation.Valid
import org.example.expert.domain.common.annotation.Auth
import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest
import org.example.expert.domain.user.dto.response.UserResponseWithImageUrl
import org.example.expert.domain.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
class UserController(
    private val userService: UserService
) {
    @GetMapping("/users/{userId}")
    fun getUser(@PathVariable userId: Long): ResponseEntity<UserResponseWithImageUrl> {
        return ResponseEntity.ok(userService.getUser(userId))
    }

    @PutMapping("/users")
    fun changePassword(@Auth authUser: AuthUser, @Valid @RequestBody userChangePasswordRequest: UserChangePasswordRequest) {
        userService.changePassword(authUser.id, userChangePasswordRequest)
    }

    @PutMapping("/users/images")
    fun uploadImage(@Auth authUser: AuthUser, @RequestParam file: MultipartFile): ResponseEntity<UserResponseWithImageUrl> {
        return ResponseEntity.ok(userService.uploadImage(authUser.id, file))
    }

    @DeleteMapping("/users/images")
    fun deleteImage(@Auth authUser: AuthUser) {
        userService.deleteImage(authUser.id)
    }
}
