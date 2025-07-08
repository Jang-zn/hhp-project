package io.hhplus.tdd.user.service;

import io.hhplus.tdd.user.User;

import java.util.Optional;

public interface UserService {
    User signup(String name);
    Optional<User> findUserById(long id);
} 