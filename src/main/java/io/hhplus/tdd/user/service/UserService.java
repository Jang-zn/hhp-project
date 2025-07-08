package io.hhplus.tdd.user.service;

import java.util.Optional;

import io.hhplus.tdd.user.model.User;

public interface UserService {
    User signup(String name);
    Optional<User> findUserById(long id);
} 