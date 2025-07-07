package io.hhplus.tdd.point.service.implement;

import io.hhplus.tdd.point.service.PointService;
import io.hhplus.tdd.point.model.UserPoint;
import io.hhplus.tdd.point.model.PointHistory;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
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
    public UserPoint getPoint(Long userId) {
        return userPointRepository.findById(userId);
    }
    
    @Override
    public UserPoint chargePoint(Long userId, Long amount) {
        // TODO: 포인트 충전 로직 구현
        return null;
    }
    
    @Override
    public UserPoint usePoint(Long userId, Long amount) {
        // TODO: 포인트 사용 로직 구현
        return null;
    }
    
    @Override
    public List<PointHistory> getPointHistories(Long userId) {
        return pointHistoryRepository.findByUserId(userId);
    }
}
