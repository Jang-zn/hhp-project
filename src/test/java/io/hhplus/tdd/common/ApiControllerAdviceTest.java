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
        ResponseEntity<ErrorResponse> response = apiControllerAdvice.handleAll(exception);
        // then
        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().code()).isEqualTo("500");
        assertThat(response.getBody().message()).isEqualTo("에러가 발생했습니다.");
    }

    @Test
    @DisplayName("IllegalArgumentException 처리 테스트")
    void handleIllegalArgumentException() {
        // given
        IllegalArgumentException exception = new IllegalArgumentException("이름은 1자 이상이어야 합니다.");
        // when
        ResponseEntity<ErrorResponse> response = apiControllerAdvice.handleAll(exception);
        // then
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().code()).isEqualTo("U004");
        assertThat(response.getBody().message()).isEqualTo("이름은 1자 이상이어야 합니다.");
    }

    @Test
    @DisplayName("HttpMessageNotReadableException 처리 테스트")
    void handleHttpMessageNotReadableException() {
        // given
        org.springframework.http.converter.HttpMessageNotReadableException exception =
            new org.springframework.http.converter.HttpMessageNotReadableException("파싱 에러", (org.springframework.http.HttpInputMessage) null);
        // when
        ResponseEntity<ErrorResponse> response = apiControllerAdvice.handleAll(exception);
        // then
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().code()).isEqualTo("400");
        assertThat(response.getBody().message()).isEqualTo("요청 파라미터 형식이 잘못되었습니다.");
    }
} 