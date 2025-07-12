package io.hhplus.tdd.point.repository.implement;

import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.model.UserPoint;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Repository;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Repository
public class UserPointRepositoryImpl implements UserPointRepository {
    
    private final UserPointTable userPointTable;
    
    // 사용자별 개별 락을 관리하는 맵. 같은 사용자에 대한 요청만 동기화됨.
    // ConcurrentHashMap을 사용해 맵 자체의 동시성 안전성도 보장.
    private final ConcurrentHashMap<Long, ReentrantLock> userLocks = new ConcurrentHashMap<>();
    
    public UserPointRepositoryImpl(UserPointTable userPointTable) {
        this.userPointTable = userPointTable;
    }
    
    /**
     * 사용자 ID에 해당하는 전용 락 객체를 반환함.
     * computeIfAbsent를 사용해 락이 없으면 새로 생성하고, 있으면 기존 것을 반환.
     * 이 방법으로 같은 사용자에 대한 모든 요청이 동일한 락을 사용하게 됨.
     */
    private ReentrantLock getUserLock(long userId) {
        return userLocks.computeIfAbsent(userId, k -> new ReentrantLock());
    }
    
    @Override
    public UserPoint findById(long id) {
        // 해당 사용자의 전용 락을 획득
        ReentrantLock lock = getUserLock(id);
        lock.lock();
        try {
            // 락 보호 하에서 데이터 조회 실행
            return userPointTable.selectById(id);
        } finally {
            // 예외 발생 여부와 관계없이 락 해제
            lock.unlock();
        }
    }
    
    @Override
    public UserPoint save(long userId, long point) {
        // 해당 사용자의 전용 락을 획득
        ReentrantLock lock = getUserLock(userId);
        lock.lock();
        try {
            // 락 보호 하에서 데이터 저장/업데이트 실행
            return userPointTable.insertOrUpdate(userId, point);
        } finally {
            // 예외 발생 여부와 관계없이 락 해제
            lock.unlock();
        }
    }
}
