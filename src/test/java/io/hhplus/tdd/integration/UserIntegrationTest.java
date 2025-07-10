package io.hhplus.tdd.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.common.constants.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("유저 API 통합 테스트")
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("성공: 유저 생성/조회")
    void userDomain_integration_success() throws Exception {
        // 유저 생성
        String name = "TestUser";
        String createRes = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"" + name + "\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value(name))
            .andReturn().getResponse().getContentAsString();

        long userId = objectMapper.readTree(createRes).get("id").asLong();

        // 유저 조회
        mockMvc.perform(get("/user/" + userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId))
            .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    @DisplayName("실패케이스: 없는 유저")
    void userNotFound_fail() throws Exception {
        mockMvc.perform(get("/user/99999"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    @DisplayName("실패케이스: 빈 이름")
    void createUser_emptyName_fail() throws Exception {
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_NAME.getCode()));
    }
}
