package com.ssafy.tlog.trip.record.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class FileUploadService {

    @Value("${file.upload.path:/uploads/images/}")
    private String uploadPath;

    @Value("${file.upload.url:http://localhost:8080/images/}")
    private String baseUrl;

    public String uploadImage(MultipartFile file) throws IOException {
        log.info("이미지 업로드 시작: {}, 크기: {}", file.getOriginalFilename(), file.getSize());

        // 파일 검증
        validateImageFile(file);

        // 업로드 디렉토리 생성
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
            log.info("업로드 디렉토리 생성: {}", uploadDir);
        }

        // 고유한 파일명 생성
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueFilename = timestamp + "_" + UUID.randomUUID().toString() + "." + extension;

        // 파일 저장
        Path filePath = uploadDir.resolve(uniqueFilename);
        Files.write(filePath, file.getBytes());

        log.info("파일 저장 완료: {}", filePath);

        // 접근 가능한 URL 반환
        String imageUrl = baseUrl + uniqueFilename;
        log.info("생성된 이미지 URL: {}", imageUrl);

        return imageUrl;
    }

    public void deleteImage(String imageUrl) {
        try {
            if (imageUrl != null && imageUrl.startsWith(baseUrl)) {
                String filename = imageUrl.substring(baseUrl.length());
                Path filePath = Paths.get(uploadPath, filename);
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    log.info("이미지 파일 삭제 완료: {}", filename);
                } else {
                    log.warn("삭제할 파일이 존재하지 않음: {}", filePath);
                }
            }
        } catch (IOException e) {
            log.error("이미지 파일 삭제 실패: {}", imageUrl, e);
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        // 파일 크기 검증 (10MB 제한)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("파일 크기는 10MB를 초과할 수 없습니다.");
        }

        // 파일 타입 검증
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }

        // 허용되는 확장자 검증
        String extension = getFileExtension(file.getOriginalFilename());
        if (!isAllowedExtension(extension)) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. (jpg, jpeg, png, gif만 가능)");
        }

        log.info("파일 검증 완료: {} ({})", file.getOriginalFilename(), contentType);
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isAllowedExtension(String extension) {
        return extension.matches("jpg|jpeg|png|gif");
    }
}