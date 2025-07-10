package io.hhplus.tdd.user.repository;

import java.util.Optional;

import io.hhplus.tdd.user.model.User;

public interface UserRepository {
    User insert(String name);

    User update(User user);

    User findById(long id);
} 