package io.hhplus.tdd.user.service;

import io.hhplus.tdd.common.constants.UserStatus;
import io.hhplus.tdd.database.UserTable;
import io.hhplus.tdd.user.model.User;
import io.hhplus.tdd.user.repository.UserRepository;
import io.hhplus.tdd.user.repository.implement.UserRepositoryImpl;
import io.hhplus.tdd.user.service.implement.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.argThat;


@DisplayName("UserServiceImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock UserRepository userRepository;
    UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @DisplayName("성공: 유저 생성")
    @Test
    void signup() {
        // given
        String name = "user1";
        User expectedUser = new User(1L, name, UserStatus.ACTIVE);
        given(userRepository.insert(name)).willReturn(expectedUser);

        // when
        User user = userService.signup(name);

        // then
        assertThat(user.name()).isEqualTo(name);
        assertThat(user.status()).isEqualTo(UserStatus.ACTIVE);
        verify(userRepository).insert(name);
    }

    @Test
    @DisplayName("성공: 사용자 조회")
    void findUserById() {
        // given
        User saved = userService.signup("findUserByIdUser");

        // when
        Optional<User> found = userService.findUserById(saved.id());

        // then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(saved);
    }

    @DisplayName("성공: 이름 변경")
    @Test
    void updateName() {
        // given
        long userId = 1L;
        String originalName = "user1";
        String newName = "userUpdated";
        User user = new User(userId, originalName, UserStatus.ACTIVE);
        User expectedUser = new User(userId, newName, UserStatus.ACTIVE);

        given(userRepository.findById(userId)).willReturn(user);
        given(userRepository.update(any(User.class))).willReturn(expectedUser);

        // when
        User updatedUser = userService.updateName(userId, newName);

        // then
        assertThat(updatedUser.name()).isEqualTo(newName);
        verify(userRepository).findById(userId);
        verify(userRepository).update(argThat(u -> u.id() == userId && u.name().equals(newName)));
    }

    @Test
    @DisplayName("성공: 사용자 탈퇴")
    void retireUser() {
        // given
        long userId = 1L;
        User user = new User(userId, "tester", UserStatus.ACTIVE);
        User expectedUser = new User(userId, "tester", UserStatus.RETIRED);

        given(userRepository.findById(userId)).willReturn(user);
        given(userRepository.update(any(User.class))).willReturn(expectedUser);

        // when
        User retiredUser = userService.retireUser(userId);

        // then
        assertThat(retiredUser.status()).isEqualTo(UserStatus.RETIRED);
        verify(userRepository).findById(userId);
        verify(userRepository).update(argThat(u -> u.id() == userId && u.status() == UserStatus.RETIRED));
    }
} 