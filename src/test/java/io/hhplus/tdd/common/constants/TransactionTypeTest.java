package io.hhplus.tdd.common.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("TransactionType 열거형 테스트")
class TransactionTypeTest {
    
    @Test
    @DisplayName("TransactionType 값 검증")
    void validateTransactionTypes() {
        // given & when & then
        assertThat(TransactionType.CHARGE).isNotNull();
        assertThat(TransactionType.USE).isNotNull();
        assertThat(TransactionType.values()).hasSize(2);
    }
} 