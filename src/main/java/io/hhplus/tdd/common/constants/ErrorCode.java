package io.hhplus.tdd.common.constants;

public enum ErrorCode {
    INSUFFICIENT_POINT("P001", "포인트가 부족합니다."),
    INVALID_AMOUNT("P002", "포인트는 0보다 커야 합니다."),
    MAX_POINT_LIMIT_EXCEEDED("P003", "최대 보유 가능 포인트를 초과했습니다."),
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다."),
    USER_NOT_ACTIVE("U002", "사용자가 활성 상태가 아닙니다."),
    DATA_INTEGRITY_VIOLATED("U003", "데이터가 손상되었습니다. 사용자의 포인트가 음수입니다."),
    POINT_HISTORY_SAVE_FAILED("P004", "포인트 이력 저장에 실패했습니다."),
    USER_IS_INACTIVE("U005", "비활성화된 사용자입니다."),
    INVALID_NAME("U004", "이름은 1자 이상이어야 합니다."),
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