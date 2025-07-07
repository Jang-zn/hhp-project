package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.model.PointHistory;
import io.hhplus.tdd.common.constants.TransactionType;
import io.hhplus.tdd.point.repository.implement.PointHistoryRepositoryImpl;
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
@DisplayName("PointHistoryRepository 구현체 단위 테스트")
class PointHistoryRepositoryImplTest {
    
    @Mock
    private PointHistoryTable pointHistoryTable;
    
    @InjectMocks
    private PointHistoryRepositoryImpl pointHistoryRepository;
    
    @Test
    @DisplayName("포인트 이력 저장 테스트")
    void save() {
        // given & when & then
        // TODO: 포인트 이력 저장 로직 테스트
    }
    
    @Test
    @DisplayName("유저별 포인트 이력 조회 테스트")
    void findByUserId() {
        // given & when & then
        // TODO: 유저별 포인트 이력 조회 로직 테스트
    }
} 