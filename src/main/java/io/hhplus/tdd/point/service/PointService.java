package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.model.UserPoint;
import io.hhplus.tdd.point.model.PointHistory;
import java.util.List;

public interface PointService {
    UserPoint getPoint(Long userId);
    UserPoint chargePoint(Long userId, Long amount);
    UserPoint usePoint(Long userId, Long amount);
    List<PointHistory> getPointHistories(Long userId);
}
