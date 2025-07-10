package io.hhplus.tdd.point.service.implement;

import io.hhplus.tdd.point.service.PointService;
import io.hhplus.tdd.point.model.UserPoint;
import io.hhplus.tdd.point.model.PointHistory;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.common.constants.ErrorCode;
import io.hhplus.tdd.common.constants.TransactionType;
import io.hhplus.tdd.user.repository.UserRepository;
import io.hhplus.tdd.user.model.User;
import io.hhplus.tdd.common.constants.UserStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PointServiceImpl implements PointService {
    
    private final long maxPoint;

    private final UserRepository userRepository;
    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    
    private final Set<Long> processingUserIds = ConcurrentHashMap.newKeySet();
    
    public PointServiceImpl(
            UserRepository userRepository,
            UserPointRepository userPointRepository,
            PointHistoryRepository pointHistoryRepository,
            @Value("${point.max-point}") long maxPoint
    ) {
        this.userRepository = userRepository;
        this.userPointRepository = userPointRepository;
        this.pointHistoryRepository = pointHistoryRepository;
        this.maxPoint = maxPoint;
    }
    
    @Override
    public UserPoint getPoint(long id) {
        // 1. 유저ID, 활성상태 검증
        validateUserId(id);
        validateActiveUser(id);
        // 2. 현재 포인트 조회
        return userPointRepository.findById(id);
    }
    
    @Override
    public UserPoint chargePoint(long userId, long amount) {
        // 1. charge 메서드 위임
        return charge(userId, amount);
    }
    
    @Override
    public UserPoint usePoint(long userId, long amount) {
        // 1. use 메서드 위임
        return use(userId, amount);
    }
    
    @Override
    public List<PointHistory> getPointHistoryList(long id) {
        // 1. 유저ID, 활성상태 검증
        validateUserId(id);
        validateActiveUser(id);
        // 2. 포인트 이력 조회
        return pointHistoryRepository.findByUserId(id);
    }

    @Override
    @Transactional // 특정 메서드만 트랜잭션 처리
    public UserPoint charge(long id, long amount) {
        // 동시성 제어: userId별 1건만 처리, 나머지는 중복 요청 에러
        if (!processingUserIds.add(id)) {
            throw new IllegalArgumentException(ErrorCode.DUPLICATE_REQUEST.getMessage());
        }
        try {
            // 1. 유저ID, 활성상태, 금액 검증
            validateUserId(id);
            validateActiveUser(id);
            validateAmount(amount);

            // 2. 현재 포인트 조회
            UserPoint current = userPointRepository.findById(id);
            if (current == null) {
                throw new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage());
            }

            long currentPoint = current.point();
            validateNonNegativePoint(currentPoint);

            // 3. 최대치 도달 여부 체크 및 실제 충전액 계산
            long newPoint = currentPoint + amount;
            if (newPoint > maxPoint) {
                throw new IllegalArgumentException(ErrorCode.MAX_POINT_LIMIT_EXCEEDED.getMessage());
            }

            // 4. 포인트 저장
            UserPoint saved = userPointRepository.save(id, newPoint);

            // 5. 이력 저장
            try {
                pointHistoryRepository.save(id, amount, TransactionType.CHARGE);
            } catch (Exception e) {
                throw new IllegalArgumentException(ErrorCode.POINT_HISTORY_SAVE_FAILED.getMessage());
            }

            // 6. 결과 반환
            return saved;
        } finally {
            processingUserIds.remove(id);
        }
    }

    @Override
    @Transactional // 특정 메서드만 트랜잭션 처리
    public UserPoint use(long id, long amount) {
        // 1. 유저ID, 활성상태, 금액 검증
        validateUserId(id);
        validateActiveUser(id);
        validateAmount(amount);

        // 2. 현재 포인트 조회
        UserPoint current = userPointRepository.findById(id);
        if (current == null) {
            throw new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage());
        }

        long currentPoint = current.point();
        validateNonNegativePoint(currentPoint);

        // 3. 포인트 차감 가능 여부 체크
        if (currentPoint < amount) {
            throw new IllegalArgumentException(ErrorCode.INSUFFICIENT_POINT.getMessage());
        }

        // 4. 포인트 차감 및 저장
        long newPoint = currentPoint - amount;
        UserPoint saved = userPointRepository.save(id, newPoint);

        // 5. 이력 저장
        try {
            pointHistoryRepository.save(id, amount, TransactionType.USE);
        } catch (Exception e) {
            throw new IllegalArgumentException(ErrorCode.POINT_HISTORY_SAVE_FAILED.getMessage());
        }

        // 6. 결과 반환
        return saved;
    }

    // ----- Validation Helpers ----- //

    private void validateUserId(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage());
        }
    }

    private void validateAmount(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(ErrorCode.INVALID_AMOUNT.getMessage());
        }
    }

    private void validateNonNegativePoint(long point) {
        if (point < 0) {
            throw new IllegalArgumentException(ErrorCode.DATA_INTEGRITY_VIOLATED.getMessage());
        }
    }

    private void validateActiveUser(long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException(ErrorCode.USER_NOT_FOUND.getMessage());
        }
        if (user.status() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException(ErrorCode.USER_IS_INACTIVE.getMessage());
        }
    }
}
