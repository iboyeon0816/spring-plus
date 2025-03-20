package org.example.expert.domain.user.service

import org.example.expert.client.S3Service
import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest
import org.example.expert.domain.user.dto.response.UserResponseWithImageUrl
import org.example.expert.domain.user.entity.User
import org.example.expert.domain.user.repository.UserRepository
import org.example.expert.domain.user.repository.findByIdOrElseThrow
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val s3Service: S3Service
) {
    fun getUser(userId: Long): UserResponseWithImageUrl {
        val user = userRepository.findByIdOrElseThrow(userId)
        val signedGetUrl = user.imageUrl?.let { s3Service.createSignedGetUrl(it) }
        return UserResponseWithImageUrl(user.id, user.email, user.nickname, signedGetUrl)
    }

    @Transactional
    fun changePassword(userId: Long, userChangePasswordRequest: UserChangePasswordRequest) {
        val user = userRepository.findByIdOrElseThrow(userId)

        validateNewPasswordNotSameAsOld(userChangePasswordRequest, user)
        validateOldPasswordMatches(userChangePasswordRequest, user)

        user.password = passwordEncoder.encode(userChangePasswordRequest.newPassword)
    }

    @Transactional
    fun uploadImage(userId: Long, file: MultipartFile): UserResponseWithImageUrl {
        val user = userRepository.findByIdOrElseThrow(userId)

        val newImageUrl = s3Service.uploadImage(file)
        user.imageUrl?.let { s3Service.deleteFile(it) }
        user.imageUrl = newImageUrl

        val signedGetUrl = s3Service.createSignedGetUrl(user.imageUrl)
        return UserResponseWithImageUrl(user.id, user.email, user.nickname, signedGetUrl)
    }

    @Transactional
    fun deleteImage(userId: Long) {
        val user = userRepository.findByIdOrElseThrow(userId)
        user.imageUrl?.let { s3Service.deleteFile(it) }
        user.imageUrl = null
    }

    private fun validateNewPasswordNotSameAsOld(request: UserChangePasswordRequest, user: User) {
        if (passwordEncoder.matches(request.newPassword, user.password)) {
            throw InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.")
        }
    }

    private fun validateOldPasswordMatches(request: UserChangePasswordRequest, user: User) {
        if (!passwordEncoder.matches(request.oldPassword, user.password)) {
            throw InvalidRequestException("잘못된 비밀번호입니다.")
        }
    }
}
