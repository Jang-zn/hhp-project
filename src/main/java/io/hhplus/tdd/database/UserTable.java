package io.hhplus.tdd.database;

import org.springframework.stereotype.Component;

import io.hhplus.tdd.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class UserTable {
    private final Map<Long, User> table = new HashMap<>();
    private final AtomicLong cursor = new AtomicLong(1);

    public User insert(String name) throws InterruptedException {
        throttle(100);
        long newId = cursor.getAndIncrement();
        User user = User.of(newId, name);
        table.put(newId, user);
        return user;
    }

    public User updateName(Long id, String newName) {
        throttle(50);
        User existingUser = table.get(id);
        User updatedUser = new User(existingUser.id(), newName, existingUser.status());
        table.put(id, updatedUser);
        return updatedUser;
    }

    public User updateStatus(Long id, io.hhplus.tdd.common.constants.UserStatus status) {
        throttle(50);
        User existingUser = table.get(id);
        User updatedUser = existingUser.withStatus(status);
        table.put(id, updatedUser);
        return updatedUser;
    }

    public User selectById(Long id) {
        throttle(50);
        return table.get(id);
    }

    private void throttle(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
} 