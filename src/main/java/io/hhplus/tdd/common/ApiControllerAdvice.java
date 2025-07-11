package io.hhplus.tdd.common;

import io.hhplus.tdd.common.constants.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Arrays;

@RestControllerAdvice
class ApiControllerAdvice {
    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class, Exception.class})
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        if (ex instanceof IllegalArgumentException) {
            ErrorCode matched = Arrays.stream(ErrorCode.values())
                .filter(e -> e.getMessage().equals(ex.getMessage()))
                .findFirst()
                .orElse(ErrorCode.SYSTEM_ERROR);
            return ResponseEntity.badRequest().body(
                new ErrorResponse(matched.getCode(), matched.getMessage())
            );
        } else if (ex instanceof HttpMessageNotReadableException) {
            return ResponseEntity.badRequest().body(
                new ErrorResponse("400", "요청 파라미터 형식이 잘못되었습니다.")
            );
        } else {
            return ResponseEntity.status(500).body(
                new ErrorResponse("500", "에러가 발생했습니다.")
            );
        }
    }
}
