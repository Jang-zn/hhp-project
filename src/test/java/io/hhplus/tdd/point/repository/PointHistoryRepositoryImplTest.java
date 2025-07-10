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


@DisplayName("PointHistoryRepositoryImpl 단위 테스트")
class PointHistoryRepositoryImplTest {
    private PointHistoryTable pointHistoryTable;
    private PointHistoryRepositoryImpl pointHistoryRepository;
    
    private static Stream<Arguments> saveSuccessPointHistoryArguments() {
        return Stream.of(
                Arguments.of(1L, 100L, TransactionType.CHARGE),
                Arguments.of(2L, 200L, TransactionType.USE),
                Arguments.of(3L, 0L, TransactionType.CHARGE),
                Arguments.of(4L, 1000L, TransactionType.USE),
                Arguments.of(5L, 50L, TransactionType.CHARGE)
        );
    }

    @BeforeEach
    void setUp() {
        pointHistoryTable = new PointHistoryTable();
        pointHistoryRepository = new PointHistoryRepositoryImpl(pointHistoryTable);
    }



    @ParameterizedTest
    @MethodSource("saveSuccessPointHistoryArguments")
    @DisplayName("성공: 다양한 거래 데이터로 포인트 이력이 정상 저장된다")
    void save(long userId, long amount, TransactionType transactionType) {
        // given
        long beforeSave = System.currentTimeMillis();
        
        // when
        PointHistory savePointHistory = pointHistoryRepository.save(userId, amount, transactionType);
        long afterSave = System.currentTimeMillis();
        
        // then
        // 입력 데이터 정확성 검증
        assertThat(savePointHistory.userId()).isEqualTo(userId);
        assertThat(savePointHistory.amount()).isEqualTo(amount);
        assertThat(savePointHistory.type()).isEqualTo(transactionType);
        
        // Repository 기능 검증 (ID 생성, 시간 기록)
        assertThat(savePointHistory.id()).isPositive();
        assertThat(savePointHistory.updateMillis()).isBetween(beforeSave, afterSave);
    }

    @Test
    @DisplayName("성공: 여러 유저의 포인트 이력이 각각 정확히 조회된다")
    void findByUserId_multipleUsers() {
        // given
        long user1Id = 1L;
        long user2Id = 2L;
        long user3Id = 3L;

        // user1의 이력 (2건)
        pointHistoryRepository.save(user1Id, 1000L, TransactionType.CHARGE);
        pointHistoryRepository.save(user1Id, 200L, TransactionType.USE);

        // user2의 이력 (1건)
        pointHistoryRepository.save(user2Id, 500L, TransactionType.CHARGE);

        // user3의 이력 (3건)
        pointHistoryRepository.save(user3Id, 800L, TransactionType.CHARGE);
        pointHistoryRepository.save(user3Id, 300L, TransactionType.USE);
        pointHistoryRepository.save(user3Id, 100L, TransactionType.USE);

        // when & then
        // 각 유저별로 본인 데이터만 조회되는지 검증
        List<PointHistory> user1Histories = pointHistoryRepository.findByUserId(user1Id);
        List<PointHistory> user2Histories = pointHistoryRepository.findByUserId(user2Id);
        List<PointHistory> user3Histories = pointHistoryRepository.findByUserId(user3Id);

        assertThat(user1Histories).hasSize(2);
        assertThat(user1Histories).extracting(PointHistory::userId).containsOnly(user1Id);

        assertThat(user2Histories).hasSize(1);
        assertThat(user2Histories).extracting(PointHistory::userId).containsOnly(user2Id);

        assertThat(user3Histories).hasSize(3);
        assertThat(user3Histories).extracting(PointHistory::userId).containsOnly(user3Id);
    }

    @Test
    @DisplayName("성공: 포인트 이력이 없는 유저 조회 시 빈 목록을 반환한다")
    void findEmptyByUserId() {
        // given
        long userIdWithNoHistory = 1L;
        
        // when
        List<PointHistory> histories = pointHistoryRepository.findByUserId(userIdWithNoHistory);

        // then
        assertThat(histories).isEmpty();
    }

    @Test
    @DisplayName("성공: 포인트 이력 조회 시 최신순(DESC)으로 정렬된다")
    void findByUserId_maintainsOrder() {
        // given
        long userId = 1L;
        PointHistory first = pointHistoryRepository.save(userId, 1000L, TransactionType.CHARGE);
        PointHistory second = pointHistoryRepository.save(userId, 200L, TransactionType.USE);
        PointHistory third = pointHistoryRepository.save(userId, 500L, TransactionType.CHARGE);

        // when
        List<PointHistory> histories = pointHistoryRepository.findByUserId(userId);

        // then
        assertThat(histories).hasSize(3);
        // 최신순 정렬: third(가장 최근) -> second -> first(가장 오래된)
        assertThat(histories.get(0).id()).isEqualTo(third.id());
        assertThat(histories.get(1).id()).isEqualTo(second.id());
        assertThat(histories.get(2).id()).isEqualTo(first.id());
    }

    @Test
    @DisplayName("성공: 존재하지 않는 유저 ID로 조회 시 빈 목록을 반환한다")
    void findByUserId_nonExistentUser() {
        // given
        long nonExistentUserId = 999L;
        // 다른 유저의 데이터는 저장
        pointHistoryRepository.save(1L, 1000L, TransactionType.CHARGE);

        // when
        List<PointHistory> histories = pointHistoryRepository.findByUserId(nonExistentUserId);

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