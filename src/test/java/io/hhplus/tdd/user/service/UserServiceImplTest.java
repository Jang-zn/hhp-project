package io.hhplus.tdd.user.service;

import io.hhplus.tdd.user.repository.UserRepository;
import io.hhplus.tdd.user.service.implement.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 구현체 단위 테스트")
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

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