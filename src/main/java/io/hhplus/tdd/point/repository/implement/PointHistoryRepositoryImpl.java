package io.hhplus.tdd.point.repository.implement;

import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.model.PointHistory;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.common.constants.TransactionType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PointHistoryRepositoryImpl implements PointHistoryRepository {
    
    private final PointHistoryTable pointHistoryTable;
    
    public PointHistoryRepositoryImpl(PointHistoryTable pointHistoryTable) {
        this.pointHistoryTable = pointHistoryTable;
    }
    
    @Override
    public PointHistory save(long userId, long amount, TransactionType type) {
        return pointHistoryTable.insert(
            userId,
            amount,
            type,
            System.currentTimeMillis()
        );
    }
    
    @Override
    public List<PointHistory> findByUserId(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }
}
