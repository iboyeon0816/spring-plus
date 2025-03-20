package org.example.expert.client;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Operations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.common.exception.ServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Service {

    @Value("${s3.bucket}")
    private String bucket;

    private final S3Operations s3Operations;

    public String uploadImage(MultipartFile file) {
        validateNotEmptyFile(file);
        validateImageType(file.getContentType());

        String key = getKey(file.getOriginalFilename());
        ObjectMetadata metadata = ObjectMetadata.builder().contentType(file.getContentType()).build();

        try (InputStream inputStream = file.getInputStream()) {
            s3Operations.upload(bucket, key, inputStream, metadata);
        } catch (IOException e) {
            log.error("[파일 업로드 실패 ]", e);
            throw new ServerException("File upload failed");
        }

        return key;
    }

    public void deleteFile(String key) {
        s3Operations.deleteObject(bucket, key);
    }

    public String createSignedGetUrl(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }
        return s3Operations.createSignedGetURL(bucket, key, Duration.ofMinutes(1)).toString();
    }

    private void validateNotEmptyFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidRequestException("File is empty");
        }
    }

    private void validateImageType(String contentType) {
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidRequestException("Invalid image type: " + contentType);
        }
    }

    private String getKey(String fileName) {
        return UUID.randomUUID() + "-" + fileName;
    }
}
