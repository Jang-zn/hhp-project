package io.hhplus.tdd.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureWebMvc
@DisplayName("UserController 통합 테스트")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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