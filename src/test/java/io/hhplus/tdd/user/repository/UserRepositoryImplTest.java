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
        // 각 테스트는 격리된 UserTable 인스턴스에서 실행된다.
        userRepository = new UserRepositoryImpl(new UserTable());
    }

    @Test
    @DisplayName("성공: 사용자를 저장하고 ID로 조회한다")
    void saveAndFindById() {
        // given
        String name = "Alice";

        // when
        User savedUser = userRepository.save(name);
        Optional<User> foundUserOpt = userRepository.findById(savedUser.id());

        // then
        assertThat(foundUserOpt).isPresent();
        User foundUser = foundUserOpt.get();

        assertThat(foundUser.id()).isEqualTo(savedUser.id());
        assertThat(foundUser.name()).isEqualTo(name);
        assertThat(foundUser.status()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("실패: 존재하지 않는 ID로 조회 시 빈 Optional을 반환한다")
    void findById_whenUserNotExists_returnsEmpty() {
        // given
        long nonExistentId = 999L;

        // when
        Optional<User> foundUserOpt = userRepository.findById(nonExistentId);

        // then
        assertThat(foundUserOpt).isEmpty();
    }

    @Test
    @DisplayName("성공: 사용자 이름을 정상적으로 수정한다")
    void updateName() {
        // given
        User user = userRepository.save("Bob");
        String newName = "BobUpdated";

        // when
        User updatedUser = userRepository.updateName(user.id(), newName);
        Optional<User> refetchedUserOpt = userRepository.findById(user.id());

        // then
        assertThat(updatedUser.name()).isEqualTo(newName);
        assertThat(refetchedUserOpt).isPresent();
        assertThat(refetchedUserOpt.get().name()).isEqualTo(newName);
    }

    @Test
    @DisplayName("성공: 사용자 상태를 정상적으로 수정한다")
    void updateStatus() {
        // given
        User user = userRepository.save("Charlie");

        // when
        User retiredUser = userRepository.updateStatus(user.id(), UserStatus.RETIRED);
        Optional<User> refetchedUserOpt = userRepository.findById(user.id());

        // then
        assertThat(retiredUser.status()).isEqualTo(UserStatus.RETIRED);
        assertThat(refetchedUserOpt).isPresent();
        assertThat(refetchedUserOpt.get().status()).isEqualTo(UserStatus.RETIRED);
    }
} 