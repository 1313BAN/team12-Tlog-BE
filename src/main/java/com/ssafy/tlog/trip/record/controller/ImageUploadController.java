package com.ssafy.tlog.trip.record.controller;

import com.ssafy.tlog.common.response.ApiResponse;
import com.ssafy.tlog.common.response.ResponseWrapper;
import com.ssafy.tlog.config.security.CustomUserDetails;
import com.ssafy.tlog.trip.record.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class ImageUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping("/image/upload")
    public ResponseEntity<ResponseWrapper<ImageUploadResponseDto>> uploadImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file) {

        try {
            String imageUrl = fileUploadService.uploadImage(file);

            ImageUploadResponseDto responseDto = ImageUploadResponseDto.builder()
                    .imageUrl(imageUrl)
                    .originalName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .build();

            return ApiResponse.success(HttpStatus.OK, "이미지 업로드가 완료되었습니다.", responseDto);

        } catch (IllegalArgumentException e) {
            log.warn("이미지 업로드 검증 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ResponseWrapper<>(400, e.getMessage(), null));
        } catch (Exception e) {
            log.error("이미지 업로드 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseWrapper<>(500, "이미지 업로드 중 오류가 발생했습니다.", null));
        }
    }

    // 응답 DTO
    @lombok.Getter
    @lombok.Setter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ImageUploadResponseDto {
        private String imageUrl;
        private String originalName;
        private long fileSize;
    }
}