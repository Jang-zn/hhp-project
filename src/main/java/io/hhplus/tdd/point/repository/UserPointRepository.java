package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.model.UserPoint;

public interface UserPointRepository {
    UserPoint findById(Long id);
    UserPoint save(UserPoint userPoint);
}
