package io.hhplus.tdd.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ApiControllerAdvice 전역 예외 처리 테스트")
class ApiControllerAdviceTest {
    
    private final ApiControllerAdvice apiControllerAdvice = new ApiControllerAdvice();
    
    @Test
    @DisplayName("일반 예외 처리 테스트")
    void handleGeneralException() {
        // given
        Exception exception = new RuntimeException("테스트 예외");
        
        // when
        ResponseEntity<ErrorResponse> response = apiControllerAdvice.handleException(exception);
        
        // then
        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().code()).isEqualTo("500");
        assertThat(response.getBody().message()).isEqualTo("에러가 발생했습니다.");
    }
} 