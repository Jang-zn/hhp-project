package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.model.UserPoint;
import io.hhplus.tdd.point.repository.implement.UserPointRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserPointRepository 구현체 단위 테스트")
class UserPointRepositoryImplTest {
    
    @Mock
    private UserPointTable userPointTable;
    
    @InjectMocks
    private UserPointRepositoryImpl userPointRepository;
    
    @Test
    @DisplayName("유저 포인트 조회 테스트")
    void findById() {
        // given
        long userId = 1L;
        UserPoint expectedUserPoint = new UserPoint(userId, 1000L, System.currentTimeMillis());
        given(userPointTable.selectById(userId)).willReturn(expectedUserPoint);
        
        // when
        UserPoint result = userPointRepository.findById(userId);
        
        // then
        assertThat(result).isEqualTo(expectedUserPoint);
    }
    
    @Test
    @DisplayName("유저 포인트 저장 테스트")
    void save() {
        // given & when & then
        // TODO: 유저 포인트 저장 로직 테스트
    }
} 