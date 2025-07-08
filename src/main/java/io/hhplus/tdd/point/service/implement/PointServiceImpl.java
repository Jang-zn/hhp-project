package io.hhplus.tdd.point.service.implement;

import io.hhplus.tdd.point.service.PointService;
import io.hhplus.tdd.point.model.UserPoint;
import io.hhplus.tdd.point.model.PointHistory;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.common.constants.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointServiceImpl implements PointService {
    
    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    
    public PointServiceImpl(UserPointRepository userPointRepository, PointHistoryRepository pointHistoryRepository) {
        this.userPointRepository = userPointRepository;
        this.pointHistoryRepository = pointHistoryRepository;
    }
    
    @Override
    public UserPoint getPoint(long id) {
        return userPointRepository.findById(id);
    }
    
    @Override
    public UserPoint chargePoint(long userId, long amount) {
        // TODO: 포인트 충전 로직 구현
        return null;
    }
    
    @Override
    public UserPoint usePoint(long userId, long amount) {
        // TODO: 포인트 사용 로직 구현
        return null;
    }
    
    @Override
    public List<PointHistory> getPointHistories(long id) {
        return pointHistoryRepository.findByUserId(id);
    }

    @Override
    public UserPoint charge(long id, long amount) {
        if (id <= 0) {
            throw new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage());
        }
        if (amount <= 0) {
            throw new IllegalArgumentException(ErrorCode.INVALID_AMOUNT.getMessage());
        }
        // TODO: 실제 포인트 충전 로직 구현
        return null;
    }

    @Override
    public UserPoint use(long id, long amount) {
        if (id <= 0) {
            throw new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage());
        }
        if (amount <= 0) {
            throw new IllegalArgumentException(ErrorCode.INVALID_AMOUNT.getMessage());
        }
        // TODO: 실제 포인트 사용 로직 구현
        return null;
    }
}
