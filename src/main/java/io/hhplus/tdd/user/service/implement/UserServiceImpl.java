package io.hhplus.tdd.user.service.implement;

import io.hhplus.tdd.user.User;
import io.hhplus.tdd.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public User signup(String name) {
        return null;
    }

    @Override
    public Optional<User> findUserById(long id) {
        return Optional.empty();
    }
} 