package io.hhplus.tdd.user.repository;

import io.hhplus.tdd.user.User;

import java.util.Optional;

public interface UserRepository {
    User save(String name);
    Optional<User> findById(long id);
} 