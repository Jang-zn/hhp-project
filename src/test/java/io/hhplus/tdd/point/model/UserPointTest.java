package io.hhplus.tdd.point.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("UserPoint 도메인 모델 테스트")
class UserPointTest {
    
    @Test
    @DisplayName("빈 UserPoint 객체 생성 시 포인트는 0이다")
    void createEmptyUserPoint() {
        // given
        long userId = 1L;
        
        // when
        UserPoint userPoint = UserPoint.empty(userId);
        
        // then
        assertThat(userPoint.id()).isEqualTo(userId);
        assertThat(userPoint.point()).isEqualTo(0L);
        assertThat(userPoint.updateMillis()).isGreaterThan(0L);
    }
    
    @Test
    @DisplayName("UserPoint 객체 생성 테스트")
    void createUserPoint() {
        // given & when & then
        // TODO: UserPoint 생성 로직 테스트
    }
} 