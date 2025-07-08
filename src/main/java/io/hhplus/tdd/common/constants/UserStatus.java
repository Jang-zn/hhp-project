package io.hhplus.tdd.common.constants;

/**
 * 유저 상태
 * - ACTIVE: 활성 상태 (정상 이용 가능)
 * - RETIRED: 탈퇴 상태 (사용자가 직접 탈퇴)
 * - SLEEPING: 휴면 상태 (장기 미로그인으로 인한 비활성)
 */
public enum UserStatus {
    ACTIVE,
    RETIRED,
    SLEEPING
}