package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.model.UserPoint;
import io.hhplus.tdd.point.model.PointHistory;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.service.implement.PointServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PointService 구현체 단위 테스트")
class PointServiceImplTest {
    
    @Mock
    private UserPointRepository userPointRepository;
    
    @Mock
    private PointHistoryRepository pointHistoryRepository;
    
    @InjectMocks
    private PointServiceImpl pointService;
    
    @Test
    @DisplayName("존재하지 않는 유저의 포인트 조회 시 0포인트를 반환한다")
    void getPointForNonExistentUser() {
        // given
        long userId = 1L;
        given(userPointRepository.findById(userId))
            .willReturn(UserPoint.empty(userId));
        
        // when
        UserPoint result = pointService.getPoint(userId);
        
        // then
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.point()).isEqualTo(0L);
    }
    
    @Test
    @DisplayName("포인트 충전 테스트")
    void chargePoint() {
        // given & when & then
        // TODO: 포인트 충전 로직 테스트
    }
    
    @Test
    @DisplayName("포인트 사용 테스트")
    void usePoint() {
        // given & when & then
        // TODO: 포인트 사용 로직 테스트
    }
    
    @Test
    @DisplayName("포인트 이력 조회 테스트")
    void getPointHistories() {
        // given & when & then
        // TODO: 포인트 이력 조회 로직 테스트
    }
} 