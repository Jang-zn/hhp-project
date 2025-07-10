package io.hhplus.tdd.point.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import io.hhplus.tdd.point.model.PointHistory;
import io.hhplus.tdd.point.model.UserPoint;
import io.hhplus.tdd.point.service.PointService;

import java.util.List;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/point")
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
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
    @GetMapping("{id}/history")
    public List<PointHistory> history(@PathVariable long id) {
        return pointService.getPointHistoryList(id);
    }

    /**
     * 특정 유저의 포인트를 충전한다.
     */
    @PatchMapping("{id}/charge")
    public ResponseEntity<?> charge(@PathVariable long id, @RequestBody Long amount) {
        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest().body(
                new io.hhplus.tdd.common.ErrorResponse(
                    io.hhplus.tdd.common.constants.ErrorCode.INVALID_AMOUNT.getCode(),
                    io.hhplus.tdd.common.constants.ErrorCode.INVALID_AMOUNT.getMessage())
            );
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
                new io.hhplus.tdd.common.ErrorResponse(
                    io.hhplus.tdd.common.constants.ErrorCode.INVALID_AMOUNT.getCode(),
                    io.hhplus.tdd.common.constants.ErrorCode.INVALID_AMOUNT.getMessage())
            );
        }
        return ResponseEntity.ok(pointService.use(id, amount));
    }
}
