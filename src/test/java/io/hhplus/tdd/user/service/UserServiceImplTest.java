package io.hhplus.tdd.user.service;

import io.hhplus.tdd.database.UserTable;
import io.hhplus.tdd.user.repository.UserRepository;
import io.hhplus.tdd.user.repository.implement.UserRepositoryImpl;
import io.hhplus.tdd.user.service.implement.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UserService 통합 테스트")
public class UserServiceImplTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        UserRepository userRepository = new UserRepositoryImpl(new UserTable());
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    @DisplayName("사용자 회원가입 테스트")
    void signup() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("사용자 조회 테스트")
    void findUserById() {
        // given

        // when

        // then
    }
} 