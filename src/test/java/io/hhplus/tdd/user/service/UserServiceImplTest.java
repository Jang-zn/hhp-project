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


@DisplayName("UserServiceImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock UserRepository userRepository;
    UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void signup() {
        // given
        String name = "user1";
        User stub = User.of(1L, name);
        when(userRepository.save(name)).thenReturn(stub);

        // when
        User saved = userService.signup(name);

        // then
        assertThat(saved).isEqualTo(stub);
        verify(userRepository).save(name);
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

    @Test
    @DisplayName("성공: 이름 변경")
    void updateName() {
        // given
        User saved = userService.signup("user1");
        String newName = "userUpdated";

        // when
        User updated = userService.updateName(saved.id(), newName);

        // then
        assertThat(updated.id()).isEqualTo(saved.id());
        assertThat(updated.name()).isEqualTo(newName);
    }

    @Test
    @DisplayName("성공: 사용자 탈퇴")
    void retireUser() {
        // given
        User saved = userService.signup("retiredUser");

        // when
        User retired = userService.retireUser(saved.id());

        // then
        assertThat(retired.status()).isEqualTo(UserStatus.RETIRED);
    }
} 