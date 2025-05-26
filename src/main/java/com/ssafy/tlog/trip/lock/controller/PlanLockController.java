package com.ssafy.tlog.trip.lock.controller;

import com.ssafy.tlog.common.response.ApiResponse;
import com.ssafy.tlog.common.response.ResponseWrapper;
import com.ssafy.tlog.config.redis.PlanLockUtil;
import com.ssafy.tlog.config.security.CustomUserDetails;
import com.ssafy.tlog.trip.lock.dto.EditLockResponse;
import com.ssafy.tlog.trip.lock.dto.EditStatusResponse;
import com.ssafy.tlog.trip.lock.dto.HeartbeatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
        import java.util.Map;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@Slf4j
public class PlanLockController {
    private final PlanLockUtil planLockUtil;

    /**
     * 편집 모드 시작 (락 획득)
     */
    @PostMapping("/{tripId}/lock")
    public ResponseEntity<ResponseWrapper<EditLockResponse>> acquireLock(
            @PathVariable int tripId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        boolean success = planLockUtil.acquirePlanLock(tripId, userDetails.getUserId());

        if (success) {
            return ApiResponse.success(
                    HttpStatus.OK,
                    "편집 모드를 시작했습니다.",
                    EditLockResponse.builder()
                            .success(true)
                            .message("편집 권한을 획득했습니다.")
                            .heartbeatInterval(30)
                            .build()
            );
        } else {
            Integer currentOwner = planLockUtil.checkPlanLock(tripId);
            return ApiResponse.success(
                    HttpStatus.CONFLICT,
                    "다른 사용자가 편집 중입니다.",
                    EditLockResponse.builder()
                            .success(false)
                            .message("다른 사용자가 편집 중입니다.")
                            .currentOwner(currentOwner)
                            .build()
            );
        }
    }

    /**
     * Heartbeat 전송 (30초마다 호출)
     */
    @PostMapping("/{tripId}/heartbeat")
    public ResponseEntity<ResponseWrapper<HeartbeatResponse>> sendHeartbeat(
            @PathVariable int tripId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        boolean success = planLockUtil.updateHeartbeat(tripId, userDetails.getUserId());

        if (success) {
            return ApiResponse.success(
                    HttpStatus.OK,
                    "Heartbeat 성공",
                    HeartbeatResponse.builder()
                            .success(true)
                            .message("Heartbeat 업데이트 성공")
                            .nextHeartbeat(30)
                            .build()
            );
        } else {
            return ApiResponse.success(
                    HttpStatus.UNAUTHORIZED,
                    "편집 권한이 없습니다.",
                    HeartbeatResponse.builder()
                            .success(false)
                            .message("편집 권한이 없습니다. 다시 편집 모드를 시작해주세요.")
                            .shouldRestart(true)
                            .build()
            );
        }
    }

    /**
     * 편집 모드 종료 (락 해제)
     */
    @DeleteMapping("/{tripId}/lock")
    public ResponseEntity<ResponseWrapper<Void>> releaseLock(
            @PathVariable int tripId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        boolean success = planLockUtil.releasePlanLock(tripId, userDetails.getUserId());

        if (success) {
            return ApiResponse.success(HttpStatus.OK, "편집 모드를 종료했습니다.");
        } else {
            return ApiResponse.success(HttpStatus.BAD_REQUEST, "락 해제에 실패했습니다.");
        }
    }

    /**
     * 편집 상태 확인
     */
    @GetMapping("/{tripId}/lock/status")
    public ResponseEntity<ResponseWrapper<EditStatusResponse>> getLockStatus(
            @PathVariable int tripId) {

        Integer currentOwner = planLockUtil.checkPlanLock(tripId);

        EditStatusResponse response = EditStatusResponse.builder()
                .isLocked(currentOwner != null)
                .currentOwner(currentOwner != null ? currentOwner : -1)
                .build();

        return ApiResponse.success(HttpStatus.OK, "편집 상태 조회 성공", response);
    }
}
