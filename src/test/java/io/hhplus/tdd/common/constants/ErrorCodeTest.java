package io.hhplus.tdd.common.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ErrorCode 열거형 테스트")
class ErrorCodeTest {
    
    @Test
    @DisplayName("ErrorCode 값 검증")
    void validateErrorCodes() {
        // given & when & then
        assertThat(ErrorCode.INSUFFICIENT_POINT.getCode()).isEqualTo("P001");
        assertThat(ErrorCode.INVALID_AMOUNT.getCode()).isEqualTo("P002");
        assertThat(ErrorCode.USER_NOT_FOUND.getCode()).isEqualTo("U001");
        assertThat(ErrorCode.SYSTEM_ERROR.getCode()).isEqualTo("S001");
    }
} 