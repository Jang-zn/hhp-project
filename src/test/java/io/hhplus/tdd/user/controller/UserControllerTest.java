package io.hhplus.tdd.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@DisplayName("UserController 웹 레이어 테스트")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("사용자 가입 API 테스트")
    void signup() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("사용자 조회 API 테스트")
    void findUserById() throws Exception {
        // given

        // when

        // then
    }
} 