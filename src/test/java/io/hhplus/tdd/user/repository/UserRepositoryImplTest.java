package io.hhplus.tdd.user.repository;

import io.hhplus.tdd.database.UserTable;
import io.hhplus.tdd.user.repository.implement.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(UserRepositoryImpl.class)
@DisplayName("UserRepositoryImpl 단위 테스트")
public class UserRepositoryImplTest {

    private UserRepositoryImpl userRepository;

    @BeforeEach
    void setUp() {
        UserTable userTable = new UserTable();
        userRepository = new UserRepositoryImpl(userTable);
    }

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