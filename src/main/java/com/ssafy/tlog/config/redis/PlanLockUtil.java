package com.ssafy.tlog.config.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanLockUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LOCK_PREFIX = "plan-lock:";
    private static final String HEARTBEAT_PREFIX = "plan-heartbeat:";
    private static final long DEFAULT_LOCK_TIMEOUT = 300; // 5분
    private static final long HEARTBEAT_GRACE_PERIOD = 90; // 90초 유예기간

    /**
     * 계획 락 획득 (기본 5분 timeout)
     */
    public boolean acquirePlanLock(int tripId, Integer userId) {
        return acquirePlanLock(tripId, userId, DEFAULT_LOCK_TIMEOUT);
    }

    /**
     * 계획 락 획득 (timeout 지정)
     */
    public boolean acquirePlanLock(int tripId, Integer userId, long timeoutSeconds) {
        String lockKey = LOCK_PREFIX + tripId;
        String heartbeatKey = HEARTBEAT_PREFIX + tripId;

        Integer currentUserId = (Integer) redisTemplate.opsForValue().get(lockKey);

        // 같은 사용자가 락을 획득한 경우, 만료 시간을 연장
        if (userId.equals(currentUserId)) {
            redisTemplate.expire(lockKey, timeoutSeconds, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(heartbeatKey, System.currentTimeMillis(), timeoutSeconds, TimeUnit.SECONDS);
            log.info("Plan lock extended: tripId={}, userId={}", tripId, userId);
            return true;
        }

        // 다른 사용자 락이 있으면 heartbeat 확인
        if (currentUserId != null) {
            Long lastHeartbeat = getLastHeartbeat(heartbeatKey);
            if (lastHeartbeat != null && (System.currentTimeMillis() - lastHeartbeat) < HEARTBEAT_GRACE_PERIOD * 1000) {
                log.info("Plan lock blocked - active user: tripId={}, currentUser={}, requestUser={}",
                        tripId, currentUserId, userId);
                return false; // 활성 락
            }
            // 비활성 락이면 삭제
            log.info("Removing inactive lock: tripId={}, inactiveUser={}", tripId, currentUserId);
            redisTemplate.delete(lockKey);
            redisTemplate.delete(heartbeatKey);
        }

        // 새 락 획득 시도
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, userId, timeoutSeconds, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(success)) {
            // 초기 heartbeat 설정
            redisTemplate.opsForValue().set(heartbeatKey, System.currentTimeMillis(), timeoutSeconds, TimeUnit.SECONDS);
            log.info("Plan lock acquired: tripId={}, userId={}", tripId, userId);
        }

        return Boolean.TRUE.equals(success);
    }

    /**
     * Heartbeat 업데이트 (락 연장)
     */
    public boolean updateHeartbeat(int tripId, Integer userId) {
        String lockKey = LOCK_PREFIX + tripId;
        String heartbeatKey = HEARTBEAT_PREFIX + tripId;

        Integer owner = (Integer) redisTemplate.opsForValue().get(lockKey);
        if (!userId.equals(owner)) {
            log.warn("Heartbeat failed - not owner: tripId={}, userId={}, owner={}", tripId, userId, owner);
            return false;
        }

        // heartbeat 업데이트 + 락 연장
        redisTemplate.expire(lockKey, DEFAULT_LOCK_TIMEOUT, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(heartbeatKey, System.currentTimeMillis(), DEFAULT_LOCK_TIMEOUT, TimeUnit.SECONDS);

        log.debug("Heartbeat updated: tripId={}, userId={}", tripId, userId);
        return true;
    }

    /**
     * 락 해제
     */
    public boolean releasePlanLock(int tripId, Integer userId) {
        String lockKey = LOCK_PREFIX + tripId;
        String heartbeatKey = HEARTBEAT_PREFIX + tripId;

        Integer owner = (Integer) redisTemplate.opsForValue().get(lockKey);
        if (!userId.equals(owner)) {
            log.warn("Lock release failed - not owner: tripId={}, userId={}, owner={}", tripId, userId, owner);
            return false;
        }

        // 락과 heartbeat 모두 삭제
        redisTemplate.delete(lockKey);
        redisTemplate.delete(heartbeatKey);

        log.info("Plan lock released: tripId={}, userId={}", tripId, userId);
        return true;
    }

    /**
     * 현재 락 소유자 확인
     */
    public Integer checkPlanLock(int tripId) {
        String lockKey = LOCK_PREFIX + tripId;
        String heartbeatKey = HEARTBEAT_PREFIX + tripId;

        Integer owner = (Integer) redisTemplate.opsForValue().get(lockKey);
        if (owner == null) return null;

        // heartbeat 확인
        Long lastHeartbeat = getLastHeartbeat(heartbeatKey);
        if (lastHeartbeat == null || (System.currentTimeMillis() - lastHeartbeat) > HEARTBEAT_GRACE_PERIOD * 1000) {
            // 비활성 락 삭제
            log.info("Cleaning inactive lock: tripId={}, owner={}", tripId, owner);
            redisTemplate.delete(lockKey);
            redisTemplate.delete(heartbeatKey);
            return null;
        }

        return owner;
    }

    /**
     * Heartbeat 타임스탬프 조회 (helper method)
     */
    private Long getLastHeartbeat(String heartbeatKey) {
        Object heartbeatObj = redisTemplate.opsForValue().get(heartbeatKey);
        if (heartbeatObj == null) return null;

        try {
            if (heartbeatObj instanceof Long) {
                return (Long) heartbeatObj;
            } else if (heartbeatObj instanceof Integer) {
                return ((Integer) heartbeatObj).longValue();
            } else {
                return Long.parseLong(heartbeatObj.toString());
            }
        } catch (NumberFormatException e) {
            log.error("Invalid heartbeat format: {}", heartbeatObj);
            return null;
        }
    }
}