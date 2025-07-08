package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.model.PointHistory;
import io.hhplus.tdd.common.constants.TransactionType;
import io.hhplus.tdd.point.repository.implement.PointHistoryRepositoryImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;


@DisplayName("PointHistoryRepository 구현체 단위 테스트")
class PointHistoryRepositoryImplTest {
    private PointHistoryTable pointHistoryTable;
    private PointHistoryRepositoryImpl pointHistoryRepository;

    @BeforeEach
    void setUp() {
        pointHistoryTable = new PointHistoryTable();
        pointHistoryRepository = new PointHistoryRepositoryImpl(pointHistoryTable);
    }

    private static Stream<Arguments> saveSuccessPointHistoryArguments() {
        return Stream.of(
                Arguments.of(1L, 100L, TransactionType.CHARGE),
                Arguments.of(2L, 200L, TransactionType.USE)
        );
    }

    @ParameterizedTest
    @MethodSource("saveSuccessPointHistoryArguments")
    @DisplayName("포인트 이력 저장 테스트")
    void save(long userId, long amount, TransactionType transactionType) {
        // given & when
        PointHistory savePointHistory = pointHistoryRepository.save(userId, amount, transactionType);
        // then
        assertThat(savePointHistory.userId()).isEqualTo(userId);
        assertThat(savePointHistory.amount()).isEqualTo(amount);
        assertThat(savePointHistory.type()).isEqualTo(transactionType);
        assertThat(savePointHistory.updateMillis()).isGreaterThan(0);
    }



    @Test
    @DisplayName("유저별 포인트 이력 목록 조회 테스트")
    void findAllByUserId() {
        // given
        
        // when
        
        // then
    }

    @Test
    @DisplayName("포인트 이력이 없는 유저 조회 시 빈 목록을 반환한다")
    void findEmptyByUserId() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("동시에 여러 저장 요청이 와도 모두 정확히 저장된다")
    void saveAll_whenConcurrentRequest() throws InterruptedException {
        // given

        // when

        // then
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L,})
    @DisplayName("실패케이스: 유효하지 않은 userId로 조회 시 에러코드를 반환한다")
    void findAllByInvalidUserId(long invalidUserId) {
        // given

        // when

        // then
    }
} 