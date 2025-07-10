package io.hhplus.tdd.point.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import io.hhplus.tdd.point.model.PointHistory;
import io.hhplus.tdd.point.model.UserPoint;
import io.hhplus.tdd.point.service.PointService;

import java.util.List;
import org.springframework.http.ResponseEntity;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import io.hhplus.tdd.common.constants.ErrorCode;
import io.hhplus.tdd.common.ErrorResponse;

@RestController
@RequestMapping("/point")
public class PointController {

    private final PointService pointService;

    // --- RateLimit 상태 관리용 ---
    // userId별로 [윈도우 시작 시각, 요청 카운트] 관리
    private final ConcurrentHashMap<Long, RateLimitWindow> rateLimitMap = new ConcurrentHashMap<>();
    private static final int RATE_LIMIT = 5;
    private static final long WINDOW_MILLIS = 1000L;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    // --- 내부 static class: userId별 윈도우 상태 ---
    private static class RateLimitWindow {
        final AtomicLong windowStart = new AtomicLong(0);
        final AtomicInteger count = new AtomicInteger(0);
    }

    /**
     * 특정 유저의 포인트를 조회한다.
     */
    @GetMapping("{id}")
    public UserPoint point(@PathVariable long id) {
        return pointService.getPoint(id);
    }

    /**
     * 특정 유저의 포인트 충전/이용 내역을 조회한다.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(@PathVariable long id) {
        return pointService.getPointHistoryList(id);
    }

    /**
     * 특정 유저의 포인트를 충전한다. (1초 5회 초과 시 429)
     */
    @PatchMapping("{id}/charge")
    public ResponseEntity<?> charge(@PathVariable long id, @RequestBody Long amount) {
        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest().body(
                new ErrorResponse(
                    ErrorCode.INVALID_AMOUNT.getCode(),
                    ErrorCode.INVALID_AMOUNT.getMessage())
            );
        }
        // --- RateLimit 체크 ---
        long now = System.currentTimeMillis();
        RateLimitWindow window = rateLimitMap.computeIfAbsent(id, k -> new RateLimitWindow());
        synchronized (window) {
            long windowStart = window.windowStart.get();
            if (now - windowStart >= WINDOW_MILLIS) {
                // 윈도우 리셋
                window.windowStart.set(now);
                window.count.set(1);
            } else {
                int cnt = window.count.incrementAndGet();
                if (cnt > RATE_LIMIT) {
                    return ResponseEntity.status(429).body(
                        new ErrorResponse(
                            ErrorCode.TOO_MANY_REQUESTS.getCode(),
                            ErrorCode.TOO_MANY_REQUESTS.getMessage())
                    );
                }
            }
        }
        return ResponseEntity.ok(pointService.charge(id, amount));
    }

    /**
     * 특정 유저의 포인트를 사용한다.
     */
    @PatchMapping("{id}/use")
    public ResponseEntity<?> use(@PathVariable long id, @RequestBody Long amount) {
        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest().body(
                new ErrorResponse(
                    ErrorCode.INVALID_AMOUNT.getCode(),
                    ErrorCode.INVALID_AMOUNT.getMessage())
            );
        }
        return ResponseEntity.ok(pointService.use(id, amount));
    }
}
