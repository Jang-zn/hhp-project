package io.hhplus.tdd.user.service.implement;

import io.hhplus.tdd.common.constants.UserStatus;
import io.hhplus.tdd.user.model.User;
import io.hhplus.tdd.user.repository.UserRepository;
import io.hhplus.tdd.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User signup(String name) {
        return userRepository.insert(name);
    }

    @Override
    public Optional<User> findUserById(long id) {
        return Optional.ofNullable(userRepository.findById(id));
    }

    @Override
    public User updateName(long id, String newName) {
        User user = userRepository.findById(id);
        User updatedUser = new User(user.id(), newName, user.status());
        return userRepository.update(updatedUser);
    }

    @Override
    public User retireUser(long id) {
        User user = userRepository.findById(id);
        User retiredUser = user.withStatus(UserStatus.RETIRED);
        return userRepository.update(retiredUser);
    }
} 