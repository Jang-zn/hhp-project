package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.model.UserPoint;
import io.hhplus.tdd.point.repository.implement.UserPointRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@WebMvcTest(UserPointRepositoryImpl.class)
@DisplayName("UserPointRepositoryImpl 단위 테스트")
class UserPointRepositoryImplTest {
    
    private UserPointTable userPointTable;
    private UserPointRepositoryImpl userPointRepository;

    private static Stream<Arguments> saveSuccessPointArguments() {
        return Stream.of(
                Arguments.of(1L, 0L),
                Arguments.of(2L, 1000L),
                Arguments.of(3L, 5000L),
                Arguments.of(999L, 10000L),
                Arguments.of(1000L, 500L)
        );
    }

    @BeforeEach
    void setUp() {
        userPointTable = new UserPointTable();
        userPointRepository = new UserPointRepositoryImpl(userPointTable);
    }

    @ParameterizedTest
    @MethodSource("saveSuccessPointArguments")
    @DisplayName("성공: 다양한 유저와 포인트로 저장이 정상 처리된다")
    void save(long userId, long point) {
        // given
        long beforeSave = System.currentTimeMillis();
        
        // when
        UserPoint savedUserPoint = userPointRepository.save(userId, point);
        long afterSave = System.currentTimeMillis();
        
        // then
        // 입력 데이터 정확성 검증
        assertThat(savedUserPoint.id()).isEqualTo(userId);
        assertThat(savedUserPoint.point()).isEqualTo(point);
        
        // Repository 기능 검증 (시간 기록)
        assertThat(savedUserPoint.updateMillis()).isBetween(beforeSave, afterSave);
    }

    @Test
    @DisplayName("성공: 기존 유저의 포인트가 새로운 값으로 업데이트된다")
    void save_updateExistingUser() {
        // given
        long userId = 1L;
        long initialPoint = 1000L;
        long updatedPoint = 2000L;
        
        userPointRepository.save(userId, initialPoint);
        
        // when
        UserPoint updatedUserPoint = userPointRepository.save(userId, updatedPoint);
        
        // then
        assertThat(updatedUserPoint.id()).isEqualTo(userId);
        assertThat(updatedUserPoint.point()).isEqualTo(updatedPoint);
        assertThat(updatedUserPoint.point()).isNotEqualTo(initialPoint);
    }

    @Test
    @DisplayName("성공: 이력이 존재하는 유저의 포인트를 정확히 조회한다")
    void findById_existingUser() {
        // given
        long userId = 1L;
        long point = 1500L;
        UserPoint savedUserPoint = userPointRepository.save(userId, point);
        
        // when
        UserPoint foundUserPoint = userPointRepository.findById(userId);
        
        // then
        assertThat(foundUserPoint.id()).isEqualTo(userId);
        assertThat(foundUserPoint.point()).isEqualTo(point);
        assertThat(foundUserPoint.updateMillis()).isEqualTo(savedUserPoint.updateMillis());
    }

    @Test
    @DisplayName("성공: 존재하지 않는 유저 조회 시 빈 포인트 객체를 반환한다")
    void findById_nonExistentUser() {
        // given
        long nonExistentUserId = 999L;
        
        // when
        UserPoint userPoint = userPointRepository.findById(nonExistentUserId);
        
        // then
        assertThat(userPoint.id()).isEqualTo(nonExistentUserId);
        assertThat(userPoint.point()).isEqualTo(0L);
        assertThat(userPoint.updateMillis()).isEqualTo(0L);
    }

} 