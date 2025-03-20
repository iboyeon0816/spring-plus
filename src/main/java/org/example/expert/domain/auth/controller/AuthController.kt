package org.example.expert.domain.auth.controller

import jakarta.validation.Valid
import org.example.expert.domain.auth.dto.request.SigninRequest
import org.example.expert.domain.auth.dto.request.SignupRequest
import org.example.expert.domain.auth.dto.response.SigninResponse
import org.example.expert.domain.auth.dto.response.SignupResponse
import org.example.expert.domain.auth.service.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/auth/signup")
    fun signup(@RequestBody @Valid signupRequest: SignupRequest): SignupResponse {
        return authService.signup(signupRequest)
    }

    @PostMapping("/auth/signin")
    fun signin(@RequestBody @Valid signinRequest: SigninRequest): SigninResponse {
        return authService.signin(signinRequest)
    }
}
