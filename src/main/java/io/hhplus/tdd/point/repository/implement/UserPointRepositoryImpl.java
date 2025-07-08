package io.hhplus.tdd.point.repository.implement;

import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.model.UserPoint;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Repository;

@Repository
public class UserPointRepositoryImpl implements UserPointRepository {
    
    private final UserPointTable userPointTable;
    
    public UserPointRepositoryImpl(UserPointTable userPointTable) {
        this.userPointTable = userPointTable;
    }
    
    @Override
    public UserPoint findById(long id) {
        return userPointTable.selectById(id);
    }
    
    @Override
    public UserPoint save(long userId, long point) {
        return userPointTable.insertOrUpdate(userId, point);
    }
}
