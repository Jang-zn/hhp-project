package io.hhplus.tdd.point.service;

import io.hhplus.tdd.common.constants.ErrorCode;
import io.hhplus.tdd.common.constants.TransactionType;
import io.hhplus.tdd.point.model.PointHistory;
import io.hhplus.tdd.point.model.UserPoint;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.service.implement.PointServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("PointServiceImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class PointServiceImplTest {

    private static final long MAX_POINT = 99_999L;

    private PointServiceImpl pointService;

    @BeforeEach
    void setUp() {
        pointService = new PointServiceImpl(userRepository, userPointRepository, pointHistoryRepository, MAX_POINT);
    }

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private io.hhplus.tdd.user.repository.UserRepository userRepository;

    // ----- 공통 헬퍼 메서드 ----- //
    /**
     * UserPoint 조회 Mock
     * - userPointRepository.findById(userId) 호출 시 "현재 포인트가 point 인 UserPoint" 를 리턴하도록 세팅한다.
     */
    private void mockUserPoint(long userId, long point) {
        when(userPointRepository.findById(userId))
                .thenReturn(new UserPoint(userId, point, System.currentTimeMillis()));
    }

    /**
     * UserPoint 저장 Mock
     * - userPointRepository.save(userId, pointAfter) 호출 시 "저장 후 포인트가 pointAfter 인 UserPoint" 를 리턴하도록 세팅한다.
     */
    private void mockSavePoint(long userId, long pointAfter) {
        when(userPointRepository.save(userId, pointAfter))
                .thenReturn(new UserPoint(userId, pointAfter, System.currentTimeMillis()));
    }

    /**
     * PointHistory 저장 Mock
     * - pointHistoryRepository.save(...) 호출 시 정상적으로 PointHistory 레코드를 리턴하도록 세팅한다.
     */
    private void mockHistorySaveSuccess(long userId, long amount, TransactionType type) {
        when(pointHistoryRepository.save(userId, amount, type))
                .thenReturn(new PointHistory(1L, userId, amount, type, System.currentTimeMillis()));
    }

    /**
     * Active User Mock
     */
    private void mockActiveUser(long userId) {
        when(userRepository.findById(userId))
                .thenReturn(new io.hhplus.tdd.user.model.User(userId, "user" + userId, io.hhplus.tdd.common.constants.UserStatus.ACTIVE));
    }

    // ----- 성공 시나리오 ----- //
    @Nested
    @DisplayName("성공 시나리오")
    class SuccessCases {
        @Test
        @DisplayName("성공: 포인트를 성공적으로 충전한다")
        void charge_success() {
            // given: 유저(1)의 보유 포인트가 1,000일 때 500원을 충전해 1,500원이 되도록 Mock 세팅
            long userId = 1L;
            long currentPoint = 1000L;
            long chargeAmount = 500L;
            long expectedPoint = currentPoint + chargeAmount;

            mockActiveUser(userId);
            mockUserPoint(userId, currentPoint);
            mockSavePoint(userId, expectedPoint);
            mockHistorySaveSuccess(userId, chargeAmount, TransactionType.CHARGE);

            // when: charge 호출
            UserPoint result = pointService.charge(userId, chargeAmount);

            // then: 저장 결과 및 Repository 호출 검증
            assertThat(result.id()).isEqualTo(userId);
            assertThat(result.point()).isEqualTo(expectedPoint);
            verify(userPointRepository).save(userId, expectedPoint);
            verify(pointHistoryRepository).save(userId, chargeAmount, TransactionType.CHARGE);
        }

        @Test
        @DisplayName("성공: 포인트를 성공적으로 사용한다")
        void use_success() {
            // given: 유저(1)의 보유 포인트가 1,000일 때 400원을 사용해 600원이 되도록 Mock 세팅
            long userId = 1L;
            long currentPoint = 1000L;
            long useAmount = 400L;
            long expectedPoint = currentPoint - useAmount;

            mockActiveUser(userId);
            mockUserPoint(userId, currentPoint);
            mockSavePoint(userId, expectedPoint);
            mockHistorySaveSuccess(userId, useAmount, TransactionType.USE);

            // when: use 호출
            UserPoint result = pointService.use(userId, useAmount);

            // then: 결과 및 저장/이력 기록 검증
            assertThat(result.id()).isEqualTo(userId);
            assertThat(result.point()).isEqualTo(expectedPoint);
            verify(userPointRepository).save(userId, expectedPoint);
            verify(pointHistoryRepository).save(userId, useAmount, TransactionType.USE);
        }

        @Test
        @DisplayName("성공: 특정 유저의 포인트 내역을 조회한다")
        void getPointHistories_success() {
            // given: 2개의 Mock PointHistory 리스트를 준비하여 Repository가 반환하도록 세팅
            long userId = 1L;
            mockActiveUser(userId);
            List<PointHistory> mockHistories = List.of(
                    new PointHistory(3L, userId, 300L, TransactionType.CHARGE, System.currentTimeMillis()),
                    new PointHistory(2L, userId, 200L, TransactionType.USE, System.currentTimeMillis())
            );
            when(pointHistoryRepository.findByUserId(userId)).thenReturn(mockHistories);

            // when: 서비스로 이력 조회 호출
            List<PointHistory> result = pointService.getPointHistoryList(userId);

            // then: 리턴된 리스트가 Mock 과 동일한지 검증
            assertThat(result).isEqualTo(mockHistories);
            verify(pointHistoryRepository).findByUserId(userId);
        }

        @Test
        @DisplayName("성공: 포인트 내역이 없는 유저 조회 시 빈 리스트를 반환한다")
        void getPointHistories_returnsEmptyList_whenNoHistoryExists() {
            // given: Repository 가 빈 리스트를 반환하도록 세팅 (이력 없음 시나리오)
            long userId = 1L;
            mockActiveUser(userId);
            when(pointHistoryRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

            // when: 이력 조회 호출
            List<PointHistory> result = pointService.getPointHistoryList(userId);

            // then: 빈 리스트 반환 확인
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("성공: 여러 스레드가 동시에 같은 userId로 요청해도 1건만 처리되고, 나머지는 중복 요청 에러가 발생한다")
        void concurrentCharge_sameUserId_onlyOneProcessed() throws Exception {
            // given: 동시 요청 환경, Mock 및 상태 준비
            int threadCount = 10; // 동시에 보낼 요청 개수
            long userId = 1L;
            long chargeAmount = 100L;

            // userPointRepository.findById: 항상 0포인트 반환 (모든 요청이 동일한 초기 상태)
            when(userPointRepository.findById(userId)).thenReturn(new UserPoint(userId, 0L, System.currentTimeMillis()));
            // userPointRepository.save: 항상 정상 저장 반환 (실제 동시성 제어는 구현체에서 해야 함)
            when(userPointRepository.save(eq(userId), anyLong())).thenReturn(new UserPoint(userId, chargeAmount, System.currentTimeMillis()));
            mockActiveUser(userId); // 유저 활성화 Mock
            mockHistorySaveSuccess(userId, chargeAmount, TransactionType.CHARGE); // 이력 저장 Mock

            // when: 여러 스레드가 동시에 charge 요청을 보냄
            ExecutorService executor = Executors.newFixedThreadPool(threadCount); // 스레드풀 생성
            CountDownLatch readyLatch = new CountDownLatch(threadCount); // 모든 스레드 준비 동기화
            CountDownLatch startLatch = new CountDownLatch(1); // 동시에 시작 신호
            CountDownLatch doneLatch = new CountDownLatch(threadCount); // 모든 작업 종료 대기
            List<Future<Object>> results = new ArrayList<>(); // 각 요청의 결과(UserPoint 또는 Exception) 저장
            for (int i = 0; i < threadCount; i++) {
                results.add(executor.submit(() -> {
                    readyLatch.countDown(); // 준비 완료 알림
                    startLatch.await(); // 시작 신호 대기
                    try {
                        // 실제 서비스 charge 호출 (동시성 제어는 구현체에서 해야 함)
                        return pointService.charge(userId, chargeAmount);
                    } catch (Exception e) {
                        // 예외 발생 시 Exception 객체 반환
                        return e;
                    } finally {
                        doneLatch.countDown(); // 작업 종료 알림
                    }
                }));
            }
            readyLatch.await(); // 모든 스레드가 준비될 때까지 대기
            startLatch.countDown(); // 모든 스레드 동시에 시작
            doneLatch.await(); // 모든 작업 종료 대기
            executor.shutdown(); // 스레드풀 종료

            // then: 1건만 성공(UserPoint), 나머지는 중복 요청 에러(Exception)여야 함
            long successCount = results.stream().filter(f -> {
                try { return f.get() instanceof UserPoint; } catch (Exception e) { return false; }
            }).count();
            long duplicateErrorCount = results.stream().filter(f -> {
                try {
                    Object r = f.get();
                    // 예외 메시지가 ErrorCode.DUPLICATE_REQUEST.getMessage()와 일치하면 중복 요청 에러로 간주
                    return r instanceof Exception && r.toString().contains(ErrorCode.DUPLICATE_REQUEST.getMessage());
                } catch (Exception e) { return false; }
            }).count();
            // 성공은 1건, 중복 에러는 나머지여야 테스트 통과
            assertThat(successCount).isEqualTo(1);
            assertThat(duplicateErrorCount).isEqualTo(threadCount - 1);
        }


        @Test
        @DisplayName("성공: 여러 userId가 동시에 요청해도 각 userId별로 1건만 처리되고, 나머지는 중복 요청 에러가 발생한다")
        void concurrentCharge_multiUserId_onlyOnePerUserProcessed() throws Exception {
            // given: 여러 userId, 동시 요청 환경, Mock 및 상태 준비
            int user1Count = 10; // user1로 보낼 요청 개수
            int user2Count = 5;  // user2로 보낼 요청 개수
            long user1 = 1L, user2 = 2L;
            long chargeAmount = 100L;

            // userPointRepository.findById: userId별 0포인트 반환
            when(userPointRepository.findById(anyLong())).thenAnswer(invocation ->
                new UserPoint(invocation.getArgument(0), 0L, System.currentTimeMillis()));
            // userPointRepository.save: 항상 정상 저장 반환
            when(userPointRepository.save(anyLong(), anyLong())).thenAnswer(invocation ->
                new UserPoint(invocation.getArgument(0), chargeAmount, System.currentTimeMillis()));
            mockActiveUser(user1);
            mockActiveUser(user2);
            mockHistorySaveSuccess(user1, chargeAmount, TransactionType.CHARGE);
            mockHistorySaveSuccess(user2, chargeAmount, TransactionType.CHARGE);

            // userIds 리스트에 user1, user2 요청을 섞어서 준비
            List<Long> userIds = new ArrayList<>();
            for (int i = 0; i < user1Count; i++) userIds.add(user1);
            for (int i = 0; i < user2Count; i++) userIds.add(user2);

            // when: 여러 userId가 동시에 charge 요청을 보냄
            ExecutorService executor = Executors.newFixedThreadPool(userIds.size());
            CountDownLatch readyLatch = new CountDownLatch(userIds.size());
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(userIds.size());
            List<Future<Object>> results = new ArrayList<>(); // 각 요청의 결과(UserPoint 또는 Exception) 저장
            for (Long uid : userIds) {
                results.add(executor.submit(() -> {
                    readyLatch.countDown(); // 준비 완료 알림
                    startLatch.await(); // 시작 신호 대기
                    try {
                        // 실제 서비스 charge 호출
                        return pointService.charge(uid, chargeAmount);
                    } catch (Exception e) {
                        // 예외 발생 시 Exception 객체 반환
                        return e;
                    } finally {
                        doneLatch.countDown(); // 작업 종료 알림
                    }
                }));
            }
            readyLatch.await(); // 모든 스레드가 준비될 때까지 대기
            startLatch.countDown(); // 모든 스레드 동시에 시작
            doneLatch.await(); // 모든 작업 종료 대기
            executor.shutdown(); // 스레드풀 종료

            // then: 각 userId별로 1건만 성공(UserPoint), 나머지는 중복 요청 에러(Exception)여야 함
            long user1Success = 0, user2Success = 0, user1Dup = 0, user2Dup = 0;
            for (int i = 0; i < results.size(); i++) {
                Long uid = userIds.get(i);
                Object r = results.get(i).get();
                if (uid.equals(user1)) {
                    if (r instanceof UserPoint) user1Success++; // user1 성공
                    else if (r instanceof Exception && r.toString().contains(ErrorCode.DUPLICATE_REQUEST.getMessage())) user1Dup++; // user1 중복 에러
                } else if (uid.equals(user2)) {
                    if (r instanceof UserPoint) user2Success++; // user2 성공
                    else if (r instanceof Exception && r.toString().contains(ErrorCode.DUPLICATE_REQUEST.getMessage())) user2Dup++; // user2 중복 에러
                }
            }
            // 각 userId별로 1건만 성공, 나머지는 중복 에러여야 테스트 통과
            assertThat(user1Success).isEqualTo(1);
            assertThat(user2Success).isEqualTo(1);
            assertThat(user1Dup).isEqualTo(user1Count - 1);
            assertThat(user2Dup).isEqualTo(user2Count - 1);
        }
    }

    // ----- 실패 시나리오 ----- //

    private static Stream<Arguments> invalidIdPointHistoryArguments() {
        return Stream.of(
                Arguments.of(-1L, 100L, TransactionType.CHARGE),
                Arguments.of(0L, 200L, TransactionType.USE)
        );
    }

    private static Stream<Arguments> invalidAmountPointHistoryArguments() {
        return Stream.of(
                Arguments.of(1L, 0L, TransactionType.CHARGE),
                Arguments.of(2L, -200L, TransactionType.USE)
        );
    }


    // ----- 실패 시나리오 ----- //

    @Test
    @DisplayName("실패: 최대 보유 포인트를 초과하면 MAX_POINT_LIMIT_EXCEEDED 예외")
    void charge_throwsException_whenItExceedsMaxLimit() {
        // given
        long userId = 1L;
        mockActiveUser(userId);
        long currentPoint = MAX_POINT;
        long chargeAmount = 1L;
        mockUserPoint(userId, currentPoint);

        // when & then
        assertThatThrownBy(() -> pointService.charge(userId, chargeAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorCode.MAX_POINT_LIMIT_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("실패: 충전 금액이 최대 보유 포인트를 초과하는 경우 MAX_POINT_LIMIT_EXCEEDED 예외")
    void charge_throwsException_whenAmountExceedsMaxPoint() {
        // given
        long userId = 1L;
        mockActiveUser(userId);
        long currentPoint = 1000L;
        long chargeAmount = MAX_POINT + 1;
        mockUserPoint(userId, currentPoint);

        // when & then
        assertThatThrownBy(() -> pointService.charge(userId, chargeAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorCode.MAX_POINT_LIMIT_EXCEEDED.getMessage());
    }


    @ParameterizedTest
    @MethodSource("invalidIdPointHistoryArguments")
    @DisplayName("실패: 잘못된 userId(0 또는 음수) 입력 시 USER_NOT_FOUND 예외")
    void chargeOrUse_throwsException_withInvalidUserIdFormat(long invalidUserId, long amount, TransactionType type) {
        assertThatThrownBy(() -> {
            if (type == TransactionType.CHARGE) pointService.charge(invalidUserId, amount);
            else pointService.use(invalidUserId, amount);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @ParameterizedTest
    @MethodSource("invalidAmountPointHistoryArguments")
    @DisplayName("실패: 잘못된 amount(0 또는 음수) 입력 시 INVALID_AMOUNT 예외")
    void chargeOrUse_throwsException_withInvalidAmount(long userId, long invalidAmount, TransactionType type) {
        if (userId > 0) mockActiveUser(userId);
        assertThatThrownBy(() -> {
            if (type == TransactionType.CHARGE) pointService.charge(userId, invalidAmount);
            else pointService.use(userId, invalidAmount);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessage(ErrorCode.INVALID_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("실패: 잔액보다 큰 금액 사용 요청 시 INSUFFICIENT_POINT 예외")
    void use_throwsException_whenBalanceIsInsufficient() {
        // given
        long userId = 1L;
        mockActiveUser(userId);
        long currentPoint = 100L;
        long useAmount = 200L;
        mockUserPoint(userId, currentPoint);
        
        // when & then
        assertThatThrownBy(() -> pointService.use(userId, useAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_POINT.getMessage());
    }

    @ParameterizedTest
    @MethodSource("transactionType")
    @DisplayName("실패: 포인트 이력 저장 실패 시 SYSTEM_ERROR 예외")
    void chargeOrUse_throwsException_whenHistorySaveFails(TransactionType type) {
        // given
        long userId = 1L;
        mockActiveUser(userId);
        long currentPoint = 1000L;
        long amount = 100L;
        mockUserPoint(userId, currentPoint);
        when(pointHistoryRepository.save(anyLong(), anyLong(), any()))
                .thenThrow(new RuntimeException("DB error"));

        // when & then
        assertThatThrownBy(() -> {
            if (type == TransactionType.CHARGE) pointService.charge(userId, amount);
            else pointService.use(userId, amount);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessage(ErrorCode.POINT_HISTORY_SAVE_FAILED.getMessage());
    }

    @ParameterizedTest
    @MethodSource("transactionType")
    @DisplayName("실패: 조회된 사용자의 포인트가 음수면 DATA_INTEGRITY_VIOLATED 예외")
    void chargeOrUse_throwsException_whenPointIsNegative(TransactionType type) {
        // given
        long userId = 1L;
        mockActiveUser(userId);
        mockUserPoint(userId, -100L); // 데이터 손상 시나리오

        // when & then
        assertThatThrownBy(() -> {
            if (type == TransactionType.CHARGE) pointService.charge(userId, 100L);
            else pointService.use(userId, 50L);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessage(ErrorCode.DATA_INTEGRITY_VIOLATED.getMessage());
    }

    @ParameterizedTest
    @MethodSource("transactionType")
    @DisplayName("실패: DB에 존재하지 않는 유저면 USER_NOT_FOUND 예외")
    void chargeOrUse_throwsException_whenUserNotFound(TransactionType type) {
        // given
        long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> {
            if (type == TransactionType.CHARGE) pointService.charge(userId, 100L);
            else pointService.use(userId, 50L);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    private static Stream<Arguments> transactionType() {
        return Stream.of(
                Arguments.of(TransactionType.CHARGE),
                Arguments.of(TransactionType.USE)
        );
    }
} 