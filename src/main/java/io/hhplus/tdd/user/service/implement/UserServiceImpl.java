package io.hhplus.tdd.user.service.implement;

import io.hhplus.tdd.user.model.User;
import io.hhplus.tdd.user.repository.UserRepository;
import io.hhplus.tdd.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User signup(String name) {
        return null;
    }

    @Override
    public Optional<User> findUserById(long id) {
        return Optional.empty();
    }

    @Override
    public User updateName(long id, String newName) {
        return null;
    }

    @Override
    public User retireUser(long id) {
        return null;
    }
} 