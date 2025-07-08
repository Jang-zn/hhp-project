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

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;


@DisplayName("PointHistoryRepository 구현체 단위 테스트")
class PointHistoryRepositoryImplTest {
    private PointHistoryTable pointHistoryTable;
    private PointHistoryRepositoryImpl pointHistoryRepository;
    
    private static Stream<Arguments> saveSuccessPointHistoryArguments() {
        return Stream.of(
                Arguments.of(1L, 100L, TransactionType.CHARGE),
                Arguments.of(2L, 200L, TransactionType.USE)
        );
    }

    @BeforeEach
    void setUp() {
        pointHistoryTable = new PointHistoryTable();
        pointHistoryRepository = new PointHistoryRepositoryImpl(pointHistoryTable);
    }



    @ParameterizedTest
    @MethodSource("saveSuccessPointHistoryArguments")
    @DisplayName("포인트 충전 및 사용 이력을 성공적으로 저장")
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
    @DisplayName("userId에 해당하는 유저의 모든 포인트 이력을 정확히 조회")
    void findAllByUserId() {
        // given
        long userId = 1L;
        pointHistoryRepository.save(userId, 1000L, TransactionType.CHARGE);
        pointHistoryRepository.save(userId, 200L, TransactionType.USE);
        pointHistoryRepository.save(userId, 300L, TransactionType.CHARGE);
        pointHistoryRepository.save(2L, 500L, TransactionType.CHARGE); // 다른 유저의 데이터

        // when
        List<PointHistory> histories = pointHistoryRepository.findByUserId(userId);

        // then
        assertThat(histories).hasSize(3);
        assertThat(histories).extracting(PointHistory::userId).containsOnly(userId);
    }

    @Test
    @DisplayName("포인트 이력이 없는 유저 조회 시 빈 목록을 반환한다")
    void findEmptyByUserId() {
        // given
        long userIdWithNoHistory = 1L;
        
        // when
        List<PointHistory> histories = pointHistoryRepository.findByUserId(userIdWithNoHistory);

        // then
        assertThat(histories).isEmpty();
    }

    @Test
    @DisplayName("동시에 여러 저장 요청이 와도 모두 정확히 저장된다")
    void saveAll_whenConcurrentRequest() throws InterruptedException {
        // given

        // when

        // then
    }
} 