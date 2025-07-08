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

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@DisplayName("UserPointRepository 구현체 단위 테스트")
class UserPointRepositoryImplTest {
    
    private UserPointTable userPointTable;
    
    private UserPointRepositoryImpl userPointRepository;

    private static Stream<Arguments> saveSuccessPointArguments() {
        return Stream.of(
                Arguments.of(1L, 1000L),
                Arguments.of(2L, 2000L)
        );
    }
    @BeforeEach
    //실제 객체 로드
    void setUp() {
        userPointTable = new UserPointTable();
        userPointRepository = new UserPointRepositoryImpl(userPointTable);
    }
    

    @ParameterizedTest
    @MethodSource("saveSuccessPointArguments")
    @DisplayName("유저 포인트 저장 테스트")
    void save(long userId, long amount) {
        // given & when
        UserPoint result = userPointRepository.save(userId, amount);

        // then
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.point()).isEqualTo(amount);
    }

    @Test
    @DisplayName("유저 포인트 조회 테스트")
    void findById() {
        // given
        long userId = 1L;
        long initialPoint = 1000L;
        userPointRepository.save(userId, initialPoint);

        // when
        UserPoint result = userPointRepository.findById(userId);
        
        // then
        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.point()).isEqualTo(initialPoint);
    }
    

} 