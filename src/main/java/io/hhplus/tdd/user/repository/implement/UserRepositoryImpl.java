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
    public User insert(String name) {
        try {
            return userTable.insert(name);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User update(User user) {
        return userTable.update(user);
    }

    @Override
    public User findById(long id) {
        return userTable.selectById(id);
    }
} 