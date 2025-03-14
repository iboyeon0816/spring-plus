package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponseWithImageUrl;
import org.example.expert.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseWithImageUrl> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/users")
    public void changePassword(@Auth AuthUser authUser, @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(authUser.getId(), userChangePasswordRequest);
    }

    @PutMapping("/users/images")
    public ResponseEntity<UserResponseWithImageUrl> uploadImage(@Auth AuthUser authUser, @RequestParam MultipartFile file) {
        return ResponseEntity.ok(userService.uploadImage(authUser.getId(), file));
    }

    @DeleteMapping("/users/images")
    public void deleteImage(@Auth AuthUser authUser) {
        userService.deleteImage(authUser.getId());
    }
}
