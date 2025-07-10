package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.model.UserPoint;
import io.hhplus.tdd.point.model.PointHistory;
import java.util.List;

public interface PointService {
    UserPoint getPoint(long id);
    UserPoint chargePoint(long userId, long amount);
    UserPoint usePoint(long userId, long amount);
    List<PointHistory> getPointHistoryList(long id);
    UserPoint charge(long id, long amount);
    UserPoint use(long id, long amount);
}
