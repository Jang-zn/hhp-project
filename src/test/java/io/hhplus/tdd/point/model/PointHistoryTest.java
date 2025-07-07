package io.hhplus.tdd.point.model;

import io.hhplus.tdd.common.constants.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PointHistory 도메인 모델 테스트")
class PointHistoryTest {
    
    @Test
    @DisplayName("PointHistory 객체 생성 테스트")
    void createPointHistory() {
        // given
        long id = 1L;
        long userId = 1L;
        long amount = 1000L;
        TransactionType type = TransactionType.CHARGE;
        long updateMillis = System.currentTimeMillis();
        
        // when
        PointHistory pointHistory = new PointHistory(id, userId, amount, type, updateMillis);
        
        // then
        assertThat(pointHistory.id()).isEqualTo(id);
        assertThat(pointHistory.userId()).isEqualTo(userId);
        assertThat(pointHistory.amount()).isEqualTo(amount);
        assertThat(pointHistory.type()).isEqualTo(type);
        assertThat(pointHistory.updateMillis()).isEqualTo(updateMillis);
    }
} 