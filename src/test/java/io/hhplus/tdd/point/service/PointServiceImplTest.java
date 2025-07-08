package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.model.UserPoint;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.repository.implement.PointHistoryRepositoryImpl;
import io.hhplus.tdd.point.repository.implement.UserPointRepositoryImpl;
import io.hhplus.tdd.point.service.implement.PointServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import io.hhplus.tdd.common.constants.ErrorCode;
import io.hhplus.tdd.common.constants.TransactionType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PointService 통합 테스트")
class PointServiceImplTest {

    private PointService pointService;

    @BeforeEach
    void setUp() {
        UserPointRepository userPointRepository = new UserPointRepositoryImpl(new UserPointTable());
        PointHistoryRepository pointHistoryRepository = new PointHistoryRepositoryImpl(new PointHistoryTable());
        pointService = new PointServiceImpl(userPointRepository, pointHistoryRepository);
    }
    private static Stream<Arguments> invalidIdPointHistoryArguments() {
        return Stream.of(
                Arguments.of(-1L, 100L, TransactionType.CHARGE),
                Arguments.of(0, 200L, TransactionType.USE)
        );
    }
    private static Stream<Arguments> invalidAmountPointHistoryArguments() {
        return Stream.of(
                Arguments.of(1L, 0L, TransactionType.CHARGE),
                Arguments.of(2L, -200L, TransactionType.USE)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidIdPointHistoryArguments")
    @DisplayName("실패케이스: 유효하지 않은 형식의 userId(0, 음수)로 요청 시 USER_NOT_FOUND 에러를 반환한다")
    void chargeOrUse_throwsException_withInvalidUserId(long invalidUserId, long amount, TransactionType transactionType) {
        // given & when & then
        assertThatThrownBy(() -> {
            if (transactionType == TransactionType.CHARGE) {
                pointService.charge(invalidUserId, amount);
            } else {
                pointService.use(invalidUserId, amount);
            }
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @ParameterizedTest
    @MethodSource("invalidAmountPointHistoryArguments")
    @DisplayName("실패케이스: 유효하지 않은 amount(0, 음수)로 요청 시 INVALID_AMOUNT 에러를 반환한다")
    void chargeOrUse_throwsException_withInvalidAmount(long userId, long invalidAmount, TransactionType transactionType) {
        // given & when & then
        assertThatThrownBy(() -> {
            if (transactionType == TransactionType.CHARGE) {
                pointService.charge(userId, invalidAmount);
            } else {
                pointService.use(userId, invalidAmount);
            }
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessage(ErrorCode.INVALID_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("포인트 충전 테스트")
    void chargePoint() {
        // given & when & then
        // TODO: 포인트 충전 로직 테스트
    }

    @Test
    @DisplayName("포인트 사용 테스트")
    void usePoint() {
        // given & when & then
        // TODO: 포인트 사용 로직 테스트
    }

    @Test
    @DisplayName("포인트 이력 조회 테스트")
    void getPointHistories() {
        // given & when & then
        // TODO: 포인트 이력 조회 로직 테스트
    }
} 