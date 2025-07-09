package io.hhplus.tdd.point.service;

import io.hhplus.tdd.common.constants.ErrorCode;
import io.hhplus.tdd.common.constants.TransactionType;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.service.implement.PointServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PointService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class PointServiceImplTest {

    @InjectMocks
    private PointServiceImpl pointService;

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;


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


    @Test
    @DisplayName("성공: 포인트를 성공적으로 충전한다")
    void charge_success() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("성공: 포인트를 성공적으로 사용한다")
    void use_success() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("성공: 특정 유저의 포인트 내역을 조회한다")
    void getPointHistories_success() {
        // given

        // when

        // then
    }
    
    @Test
    @DisplayName("성공(엣지케이스): 포인트 내역이 없는 유저 조회 시 빈 리스트를 반환한다")
    void getPointHistories_returnsEmptyList_whenNoHistoryExists() {
        // given

        // when

        // then
    }

    @ParameterizedTest
    @MethodSource("invalidIdPointHistoryArguments")
    @DisplayName("실패케이스 : 유효하지 않은 형식의 userId(0, 음수)로 요청 시 예외(USER_NOT_FOUND)를 반환한다")
    void chargeOrUse_throwsException_withInvalidUserIdFormat(long invalidUserId, long amount, TransactionType transactionType) {
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
    @DisplayName("실패케이스 : 유효하지 않은 amount(0, 음수)로 요청 시 예외(INVALID_AMOUNT)를 반환한다")
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
    @DisplayName("실패케이스 : 잔액보다 큰 금액 사용 요청 시 예외(INSUFFICIENT_POINT)를 반환한다")
    void use_throwsException_whenBalanceIsInsufficient() {
        // given

        // when & then

    }
    
    @Test
    @DisplayName("실패케이스 : 충전 시 최대 보유 가능 포인트를 초과하면 예외(MAX_POINT_LIMIT_EXCEEDED)를 반환한다")
    void charge_throwsException_whenItExceedsMaxLimit() {
        // given

        // when & then

    }

    @Test
    @DisplayName("실패케이스 : 산술 오버플로우 발생 가능 값으로 충전 시 예외(ARITHMETIC_OVERFLOW)를 반환한다")
    void charge_throwsException_onArithmeticOverflow() {
        // given

        // when & then

    }
    
    @ParameterizedTest
    @MethodSource("transactionType")
    @DisplayName("실패케이스 : 포인트 이력 저장 실패 시 예외(POINT_HISTORY_SAVE_FAILED)를 반환한다")
    void chargeOrUse_throwsException_whenHistorySaveFails(TransactionType transactionType) {
        // given

        // when & then

    }

    @ParameterizedTest
    @MethodSource("transactionType")
    @DisplayName("실패케이스 : 조회된 사용자의 포인트가 음수면 예외(DATA_INTEGRITY_VIOLATED)를 반환한다")
    void chargeOrUse_throwsException_whenPointIsNegative(TransactionType transactionType) {
        // given

        // when & then

    }

    @ParameterizedTest
    @MethodSource("transactionType")
    @DisplayName("실패케이스 : DB에 존재하지 않는 유저로 요청 시 예외(USER_NOT_FOUND)를 반환한다")
    void chargeOrUse_throwsException_whenUserNotFound(TransactionType transactionType) {
        // given

        // when & then

    }

    @ParameterizedTest
    @MethodSource("transactionType")
    @DisplayName("실패케이스 : 비활성화/탈퇴한 유저로 요청 시 예외(USER_IS_INACTIVE)를 반환한다")
    void chargeOrUse_throwsException_whenUserIsInactive(TransactionType transactionType) {
        // given

        // when & then

    }

    private static Stream<Arguments> transactionType() {
        return Stream.of(
                Arguments.of(TransactionType.CHARGE),
                Arguments.of(TransactionType.USE)
        );
    }
} 