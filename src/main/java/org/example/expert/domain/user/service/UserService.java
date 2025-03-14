package org.example.expert.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.S3Service;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponseWithImageUrl;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    public UserResponseWithImageUrl getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        String signedGetUrl = s3Service.createSignedGetUrl(user.getImageUrl());
        return new UserResponseWithImageUrl(user.getId(), user.getEmail(), user.getNickname(), signedGetUrl);
    }

    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest userChangePasswordRequest) {
        validateNewPassword(userChangePasswordRequest);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        if (passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }

        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }

    @Transactional
    public UserResponseWithImageUrl uploadImage(long userId, MultipartFile file) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));

        // 이미지 업로드
        String newImageUrl = s3Service.uploadImage(file);

        // 기존 이미지 삭제
        String prevImageUrl = user.getImageUrl();
        if (prevImageUrl != null && !prevImageUrl.isEmpty()) {
            s3Service.deleteFile(prevImageUrl);
        }

        user.updateImageUrl(newImageUrl);

        String signedGetUrl = s3Service.createSignedGetUrl(user.getImageUrl());
        return new UserResponseWithImageUrl(user.getId(), user.getEmail(), user.getNickname(), signedGetUrl);
    }

    @Transactional
    public void deleteImage(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));

        String prevImageUrl = user.getImageUrl();
        if (prevImageUrl != null && !prevImageUrl.isEmpty()) {
            s3Service.deleteFile(prevImageUrl);
        }

        user.updateImageUrl(null);
    }

    private static void validateNewPassword(UserChangePasswordRequest userChangePasswordRequest) {
        if (userChangePasswordRequest.getNewPassword().length() < 8 ||
                !userChangePasswordRequest.getNewPassword().matches(".*\\d.*") ||
                !userChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {
            throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }
    }
}
