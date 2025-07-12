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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

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
        //given: 입력값 준비함
        long beforeSave = System.currentTimeMillis();
        //when: 포인트 저장함
        UserPoint savedUserPoint = userPointRepository.save(userId, point);
        long afterSave = System.currentTimeMillis();
        //then: 입력 데이터, id, 시간 기록 검증함
        assertThat(savedUserPoint.id()).isEqualTo(userId);
        assertThat(savedUserPoint.point()).isEqualTo(point);
        assertThat(savedUserPoint.updateMillis()).isBetween(beforeSave, afterSave);
    }

    @Test
    @DisplayName("성공: 기존 유저의 포인트가 새로운 값으로 업데이트된다")
    void save_updateExistingUser() {
        //given: 기존 유저 포인트 저장함
        long userId = 1L;
        long initialPoint = 1000L;
        long updatedPoint = 2000L;
        userPointRepository.save(userId, initialPoint);
        //when: 같은 유저 id로 포인트 업데이트함
        UserPoint updatedUserPoint = userPointRepository.save(userId, updatedPoint);
        //then: 포인트가 새 값으로 바뀌었는지 검증함
        assertThat(updatedUserPoint.id()).isEqualTo(userId);
        assertThat(updatedUserPoint.point()).isEqualTo(updatedPoint);
        assertThat(updatedUserPoint.point()).isNotEqualTo(initialPoint);
    }

    @Test
    @DisplayName("성공: 이력이 존재하는 유저의 포인트를 정확히 조회한다")
    void findById_existingUser() {
        //given: 유저 포인트 저장함
        long userId = 1L;
        long point = 1500L;
        UserPoint savedUserPoint = userPointRepository.save(userId, point);
        //when: 저장된 유저 포인트 조회함
        UserPoint foundUserPoint = userPointRepository.findById(userId);
        //then: 저장한 값이랑 같은지 검증함
        assertThat(foundUserPoint.id()).isEqualTo(userId);
        assertThat(foundUserPoint.point()).isEqualTo(point);
        assertThat(foundUserPoint.updateMillis()).isEqualTo(savedUserPoint.updateMillis());
    }

    @Test
    @DisplayName("성공: 존재하지 않는 유저 조회 시 빈 포인트 객체를 반환한다")
    void findById_nonExistentUser() {
        //given: 없는 유저 id 준비함
        long nonExistentUserId = 999L;
        //when: 없는 유저 조회함
        UserPoint userPoint = userPointRepository.findById(nonExistentUserId);
        //then: id만 같고 포인트는 0인지 검증함
        assertThat(userPoint.id()).isEqualTo(nonExistentUserId);
        assertThat(userPoint.point()).isEqualTo(0L);
        assertThat(userPoint.updateMillis()).isNotEqualTo(0L);
    }

    @Test
    @DisplayName("성공: 여러 스레드가 동시에 포인트를 충전해도 데이터 정합성이 보장된다")
    void save_concurrently_and_ensure_consistency() throws InterruptedException {
        //given: 동시성 테스트용 스레드 수, 유저 id, 충전 금액, latch, 스레드풀 준비함
        int threadCount = 20;
        long userId = 1L;
        long chargeAmount = 50L;
        userPointRepository.save(userId, 0L);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        //when: 여러 스레드가 동시에 '조회-계산-저장' 로직 실행함
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    UserPoint currentUserPoint = userPointRepository.findById(userId);
                    userPointRepository.save(userId, currentUserPoint.point() + chargeAmount);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();
        //then: 모든 충전이 반영되어 최종 포인트가 기대값과 같은지 검증함
        UserPoint finalUserPoint = userPointRepository.findById(userId);
        long expectedPoint = chargeAmount * threadCount;
        assertThat(finalUserPoint.point()).isEqualTo(expectedPoint);
    }

    @Test
    @DisplayName("성공: 여러 다른 유저에게 동시에 포인트 충전 요청이 들어와도 각 유저의 데이터 정합성이 보장된다")
    void save_concurrently_for_multiple_users_and_ensure_consistency() throws InterruptedException {
        //given: 여러 유저, 각 유저별 요청 횟수, 충전 금액, latch, 스레드풀 준비함
        long chargeAmount = 100L;
        Map<Long, Integer> userRequestCounts = Map.of(
            1L, 10,
            2L, 15,
            3L, 20
        );
        int totalThreads = userRequestCounts.values().stream().mapToInt(Integer::intValue).sum();
        userRequestCounts.keySet().forEach(userId -> userPointRepository.save(userId, 0L));
        ExecutorService executorService = Executors.newFixedThreadPool(totalThreads);
        CountDownLatch latch = new CountDownLatch(totalThreads);
        List<Runnable> tasks = new ArrayList<>();
        userRequestCounts.forEach((userId, count) -> {
            for (int i = 0; i < count; i++) {
                tasks.add(() -> {
                    try {
                        UserPoint currentUserPoint = userPointRepository.findById(userId);
                        userPointRepository.save(userId, currentUserPoint.point() + chargeAmount);
                    } finally {
                        latch.countDown();
                    }
                });
            }
        });
        Collections.shuffle(tasks);
        tasks.forEach(executorService::submit);
        latch.await();
        executorService.shutdown();
        //then: 각 유저별로 최종 포인트가 기대값과 같은지 검증함
        userRequestCounts.forEach((userId, count) -> {
            UserPoint finalUserPoint = userPointRepository.findById(userId);
            long expectedPoint = chargeAmount * count;
            assertThat(finalUserPoint.point())
                .as("유저 ID %d의 최종 포인트 검증", userId)
                .isEqualTo(expectedPoint);
        });
    }
} 