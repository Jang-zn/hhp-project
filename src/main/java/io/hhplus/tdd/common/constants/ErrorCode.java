package io.hhplus.tdd.common.constants;

public enum ErrorCode {
    INSUFFICIENT_POINT("P001", "포인트가 부족합니다."),
    INVALID_AMOUNT("P002", "잘못된 금액입니다."),
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다."),
    SYSTEM_ERROR("S001", "시스템 오류가 발생했습니다.");
    
    private final String code;
    private final String message;
    
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}