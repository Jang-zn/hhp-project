package io.hhplus.tdd.user.repository;

import java.util.Optional;

import io.hhplus.tdd.user.model.User;

public interface UserRepository {
    User save(String name);
    Optional<User> findById(long id);
    User updateName(long id, String name);
    User updateStatus(long id, io.hhplus.tdd.common.constants.UserStatus status);
} 