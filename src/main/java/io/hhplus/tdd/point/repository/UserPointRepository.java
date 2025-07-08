package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.model.UserPoint;

public interface UserPointRepository {
    UserPoint findById(long userid);
    UserPoint save(long userId, long point);
}
