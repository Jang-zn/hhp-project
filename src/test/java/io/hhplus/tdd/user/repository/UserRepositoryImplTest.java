package io.hhplus.tdd.user.repository;

import io.hhplus.tdd.database.UserTable;
import io.hhplus.tdd.user.repository.implement.UserRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRepository 구현체 단위 테스트")
public class UserRepositoryImplTest {

    @Mock
    private UserTable userTable;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    @Test
    @DisplayName("사용자 저장 테스트")
    void save() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("사용자 ID로 조회 테스트")
    void findById() {
        // given

        // when

        // then
    }
} 