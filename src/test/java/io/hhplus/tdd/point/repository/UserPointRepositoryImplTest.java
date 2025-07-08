package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.model.UserPoint;
import io.hhplus.tdd.point.repository.implement.UserPointRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("UserPointRepository 구현체 단위 테스트")
class UserPointRepositoryImplTest {
    
    private UserPointTable userPointTable;
    
    private UserPointRepositoryImpl userPointRepository;
    
    @BeforeEach
    //실제 객체 로드
    void setUp() {
        userPointTable = new UserPointTable();
        userPointRepository = new UserPointRepositoryImpl(userPointTable);
    }
    
    @Test
    @DisplayName("유저 포인트 조회 테스트")
    void findById() {
        // given
        long userId = 1L;
        userPointTable.insertOrUpdate(userId, 1000L);
        
        // when
        UserPoint result = userPointRepository.findById(userId);
        
        // then
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.point()).isEqualTo(1000L);
    }
    
    @Test
    @DisplayName("유저 포인트 저장 테스트")
    void save() {
        // given
        long userId = 1L;
        long amount = 500L;

        // when
        UserPoint result = userPointRepository.save(new UserPoint(userId, amount, System.currentTimeMillis()));

        // then
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.point()).isEqualTo(amount);
    }
} 