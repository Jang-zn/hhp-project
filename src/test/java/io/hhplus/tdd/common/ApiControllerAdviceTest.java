package io.hhplus.tdd.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.HttpInputMessage;
import static org.assertj.core.api.Assertions.*;

@DisplayName("ApiControllerAdvice 전역 예외 처리 테스트")
class ApiControllerAdviceTest {
    
    private final ApiControllerAdvice apiControllerAdvice = new ApiControllerAdvice();
    
    @Test
    @DisplayName("일반 예외 처리 테스트")
    void handleGeneralException() {
        //given: RuntimeException 발생시킴
        Exception exception = new RuntimeException("테스트 예외");
        //when: 예외 처리 핸들러 호출함
        ResponseEntity<ErrorResponse> response = apiControllerAdvice.handleAll(exception);
        //then: 500 에러 코드와 기본 메시지 반환하는지 검증함
        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().code()).isEqualTo("500");
        assertThat(response.getBody().message()).isEqualTo("에러가 발생했습니다.");
    }

    @Test
    @DisplayName("IllegalArgumentException 처리 테스트")
    void handleIllegalArgumentException() {
        //given: IllegalArgumentException 발생시킴
        IllegalArgumentException exception = new IllegalArgumentException("이름은 1자 이상이어야 합니다.");
        //when: 예외 처리 핸들러 호출함
        ResponseEntity<ErrorResponse> response = apiControllerAdvice.handleAll(exception);
        //then: 400 에러 코드와 커스텀 메시지 반환하는지 검증함
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().code()).isEqualTo("U004");
        assertThat(response.getBody().message()).isEqualTo("이름은 1자 이상이어야 합니다.");
    }

    @Test
    @DisplayName("HttpMessageNotReadableException 처리 테스트")
    void handleHttpMessageNotReadableException() {
        //given: HttpMessageNotReadableException 발생시킴
        HttpMessageNotReadableException exception =
            new HttpMessageNotReadableException("파싱 에러", (HttpInputMessage) null);
        //when: 예외 처리 핸들러 호출함
        ResponseEntity<ErrorResponse> response = apiControllerAdvice.handleAll(exception);
        //then: 400 에러 코드와 파라미터 형식 에러 메시지 반환하는지 검증함
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().code()).isEqualTo("400");
        assertThat(response.getBody().message()).isEqualTo("요청 파라미터 형식이 잘못되었습니다.");
    }
} 