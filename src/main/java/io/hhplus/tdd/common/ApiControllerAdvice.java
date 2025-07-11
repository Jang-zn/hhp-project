package io.hhplus.tdd.common;

import io.hhplus.tdd.common.constants.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorCode matched = Arrays.stream(ErrorCode.values())
            .filter(e -> e.getMessage().equals(ex.getMessage()))
            .findFirst()
            .orElse(ErrorCode.SYSTEM_ERROR);
        return ResponseEntity.badRequest().body(
            new ErrorResponse(matched.getCode(), matched.getMessage())
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(
            new ErrorResponse("400", "요청 파라미터 형식이 잘못되었습니다.")
        );
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("500", "에러가 발생했습니다."));
    }
}
