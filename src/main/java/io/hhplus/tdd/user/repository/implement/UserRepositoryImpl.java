package io.hhplus.tdd.user.repository.implement;

import io.hhplus.tdd.database.UserTable;
import io.hhplus.tdd.user.model.User;
import io.hhplus.tdd.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserTable userTable;

    @Override
    public User save(String name) {
        try {
            return userTable.insert(name);
        } catch (InterruptedException e) {
            // In a real application, a more specific exception should be used.
            throw new RuntimeException("Error while saving user", e);
        }
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(userTable.selectById(id));
    }
} 