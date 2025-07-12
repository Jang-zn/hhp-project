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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        //given: 입력값 준비함
        long beforeSave = System.currentTimeMillis();
        //when: 포인트 이력 저장함
        PointHistory savePointHistory = pointHistoryRepository.save(userId, amount, transactionType);
        long afterSave = System.currentTimeMillis();
        //then: 입력 데이터, id, 시간 기록 검증함
        assertThat(savePointHistory.userId()).isEqualTo(userId);
        assertThat(savePointHistory.amount()).isEqualTo(amount);
        assertThat(savePointHistory.type()).isEqualTo(transactionType);
        assertThat(savePointHistory.id()).isPositive();
        assertThat(savePointHistory.updateMillis()).isBetween(beforeSave, afterSave);
    }

    @Test
    @DisplayName("성공: 여러 유저의 포인트 이력이 각각 정확히 조회된다")
    void findByUserId_multipleUsers() {
        //given: 여러 유저의 포인트 이력 저장함
        long user1Id = 1L;
        long user2Id = 2L;
        long user3Id = 3L;
        pointHistoryRepository.save(user1Id, 1000L, TransactionType.CHARGE);
        pointHistoryRepository.save(user1Id, 200L, TransactionType.USE);
        pointHistoryRepository.save(user2Id, 500L, TransactionType.CHARGE);
        pointHistoryRepository.save(user3Id, 800L, TransactionType.CHARGE);
        pointHistoryRepository.save(user3Id, 300L, TransactionType.USE);
        pointHistoryRepository.save(user3Id, 100L, TransactionType.USE);
        //when: 각 유저별 이력 조회함
        List<PointHistory> user1HistoryList = pointHistoryRepository.findByUserId(user1Id);
        List<PointHistory> user2HistoryList = pointHistoryRepository.findByUserId(user2Id);
        List<PointHistory> user3HistoryList = pointHistoryRepository.findByUserId(user3Id);
        //then: 본인 데이터만 정확히 조회되는지 검증함
        assertThat(user1HistoryList).hasSize(2);
        assertThat(user1HistoryList).extracting(PointHistory::userId).containsOnly(user1Id);
        assertThat(user2HistoryList).hasSize(1);
        assertThat(user2HistoryList).extracting(PointHistory::userId).containsOnly(user2Id);
        assertThat(user3HistoryList).hasSize(3);
        assertThat(user3HistoryList).extracting(PointHistory::userId).containsOnly(user3Id);
    }

    @Test
    @DisplayName("성공: 포인트 이력이 없는 유저 조회 시 빈 목록을 반환한다")
    void findEmptyByUserId() {
        //given: 이력 없는 유저 id 준비함
        long userIdWithNoHistory = 1L;
        //when: 이력 조회함
        List<PointHistory> historyList = pointHistoryRepository.findByUserId(userIdWithNoHistory);
        //then: 빈 목록 반환하는지 검증함
        assertThat(historyList).isEmpty();
    }

    @Test
    @DisplayName("성공: 포인트 이력 조회 시 최신순(DESC)으로 정렬된다")
    void findByUserId_maintainsOrder() {
        //given: 여러 건 저장함
        long userId = 1L;
        PointHistory first = pointHistoryRepository.save(userId, 1000L, TransactionType.CHARGE);
        PointHistory second = pointHistoryRepository.save(userId, 200L, TransactionType.USE);
        PointHistory third = pointHistoryRepository.save(userId, 500L, TransactionType.CHARGE);
        //when: 이력 조회함
        List<PointHistory> historyList = pointHistoryRepository.findByUserId(userId);
        //then: 최신순 정렬(가장 최근이 0번)에 맞는지 검증함
        assertThat(historyList).hasSize(3);
        assertThat(historyList.get(0).id()).isEqualTo(third.id());
        assertThat(historyList.get(1).id()).isEqualTo(second.id());
        assertThat(historyList.get(2).id()).isEqualTo(first.id());
    }

    @Test
    @DisplayName("성공: 존재하지 않는 유저 ID로 조회 시 빈 목록을 반환한다")
    void findByUserId_nonExistentUser() {
        //given: 없는 유저 id 준비함
        long nonExistentUserId = 999L;
        pointHistoryRepository.save(1L, 1000L, TransactionType.CHARGE);
        //when: 없는 유저로 이력 조회함
        List<PointHistory> historyList = pointHistoryRepository.findByUserId(nonExistentUserId);
        //then: 빈 목록 반환하는지 검증함
        assertThat(historyList).isEmpty();
    }

    @Test
    @DisplayName("성공: 포인트 이력 동시 저장 테스트")
    void saveAll_whenConcurrentRequest() throws InterruptedException {
        //given: 동시 저장 요청 개수, latch, 스레드풀 준비함
        int threadCount = 20;
        long userId = 1L;
        long amount = 100L;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        //when: 여러 스레드가 동시에 save 호출함
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    pointHistoryRepository.save(userId, amount, TransactionType.CHARGE);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();
        //then: 저장된 이력 개수 검증함
        List<PointHistory> historyList = pointHistoryRepository.findByUserId(userId);
        assertThat(historyList).hasSize(threadCount);
    }
} 