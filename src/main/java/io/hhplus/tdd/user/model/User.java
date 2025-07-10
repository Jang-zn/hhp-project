package io.hhplus.tdd.user.model;

import io.hhplus.tdd.common.constants.UserStatus;

public record User(long id, String name, UserStatus status) {
    public static User of(long id, String name) {
        return new User(id, name, UserStatus.ACTIVE);
    }

    public User withStatus(UserStatus status) {
        return new User(id, name, status);
    }
} 