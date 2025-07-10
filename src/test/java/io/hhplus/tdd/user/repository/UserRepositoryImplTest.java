package io.hhplus.tdd.user.repository;

import io.hhplus.tdd.common.constants.UserStatus;
import io.hhplus.tdd.database.UserTable;
import io.hhplus.tdd.user.model.User;
import io.hhplus.tdd.user.repository.implement.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserRepositoryImpl 단위 테스트")
class UserRepositoryImplTest {

    private UserRepositoryImpl userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryImpl(new UserTable());
    }

    @Test
    @DisplayName("성공: 사용자 정보를 저장한다")
    void save() {
        // given
        String name = "tester";

        // when
        User user = userRepository.insert(name);

        // then
        assertThat(user.name()).isEqualTo(name);
        assertThat(user.status()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("성공: 아이디로 사용자를 조회한다")
    void findById() {
        // given
        User savedUser = userRepository.insert("tester");

        // when
        User user = userRepository.findById(savedUser.id());

        // then
        assertThat(user.id()).isEqualTo(savedUser.id());
        assertThat(user.name()).isEqualTo(savedUser.name());
    }

    @DisplayName("유저 정보 수정 테스트")
    @Test
    void update() {
        // given
        User user = userRepository.insert("tester");
        String newName = "tester-2";
        UserStatus newStatus = UserStatus.RETIRED;

        // when
        User updatedUser = userRepository.update(new User(user.id(), newName, newStatus));

        // then
        assertThat(updatedUser.id()).isEqualTo(user.id());
        assertThat(updatedUser.name()).isEqualTo(newName);
        assertThat(updatedUser.status()).isEqualTo(newStatus);
    }
} 