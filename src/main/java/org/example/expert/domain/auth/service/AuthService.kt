package org.example.expert.domain.auth.service

import org.example.expert.config.JwtUtil
import org.example.expert.domain.auth.dto.request.SigninRequest
import org.example.expert.domain.auth.dto.request.SignupRequest
import org.example.expert.domain.auth.dto.response.SigninResponse
import org.example.expert.domain.auth.dto.response.SignupResponse
import org.example.expert.domain.auth.exception.AuthException
import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.user.entity.User
import org.example.expert.domain.user.enums.UserRole.Companion.of
import org.example.expert.domain.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {
    @Transactional
    fun signup(signupRequest: SignupRequest): SignupResponse {
        validateEmailDuplication(signupRequest)

        val encodedPassword = passwordEncoder.encode(signupRequest.password)
        val userRole = of(signupRequest.userRole)
        val user = User(signupRequest.email, encodedPassword, signupRequest.nickname, userRole)

        userRepository.save(user)

        val bearerToken = jwtUtil.createToken(user.id, user.email, user.nickname, userRole)
        return SignupResponse(bearerToken)
    }

    fun signin(signinRequest: SigninRequest): SigninResponse {
        val user = userRepository.findByEmail(signinRequest.email) ?: throw InvalidRequestException("가입되지 않은 유저입니다.")

        validatePasswordMatches(signinRequest, user)

        val bearerToken = jwtUtil.createToken(user.id, user.email, user.nickname, user.userRole)
        return SigninResponse(bearerToken)
    }

    private fun validateEmailDuplication(signupRequest: SignupRequest) {
        if (userRepository.existsByEmail(signupRequest.email)) {
            throw InvalidRequestException("이미 존재하는 이메일입니다.")
        }
    }

    private fun validatePasswordMatches(signinRequest: SigninRequest, user: User) {
        if (!passwordEncoder.matches(signinRequest.password, user.password)) {
            throw AuthException("잘못된 비밀번호입니다.")
        }
    }
}
