package io.hhplus.tdd.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import io.hhplus.tdd.user.model.User;
import io.hhplus.tdd.user.service.UserService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.Optional;
import io.hhplus.tdd.common.constants.UserStatus;
import io.hhplus.tdd.common.constants.ErrorCode;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WebMvcTest(UserController.class)
@DisplayName("UserController 단위 테스트")
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
        String name = "user1";
        User expectedUser = User.of(1L, name);
        given(userService.signup(name)).willReturn(expectedUser);

        // when & then
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", name))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedUser.id()))
                .andExpect(jsonPath("$.name").value(name));

        verify(userService).signup(name);
    }

    @Test
    @DisplayName("실패: 사용자 가입 - 빈 이름")
    void signup_emptyName_fail() throws Exception {
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("name", ""))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_NAME.getCode()));
    }

    @Test
    @DisplayName("실패: 사용자 가입 - 공백 이름")
    void signup_blankName_fail() throws Exception {
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("name", "   "))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_NAME.getCode()));
    }

    @Test
    @DisplayName("사용자 조회 API 테스트")
    void findUserById() throws Exception {
        // given
        long userId = 1L;
        String name = "user1";
        User user = User.of(userId, name);
        given(userService.findUserById(userId)).willReturn(Optional.of(user));

        // when & then
        mockMvc.perform(get("/user/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(name));

        verify(userService).findUserById(userId);
    }

    @Test
    @DisplayName("실패케이스: 없는 유저")
    void userNotFound_fail() {
        assertThatThrownBy(() ->
            userService.findUserById(99999L)
                .orElseThrow(() -> new RuntimeException(ErrorCode.USER_NOT_FOUND.getMessage()))
        )
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("사용자 이름 수정 API 테스트")
    void updateUserName() throws Exception {
        // given
        long userId = 1L;
        String newName = "updatedName";
        User updatedUser = User.of(userId, newName);
        given(userService.updateName(userId, newName)).willReturn(updatedUser);

        // when & then
        mockMvc.perform(patch("/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", newName))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(newName));

        verify(userService).updateName(userId, newName);
    }

    @Test
    @DisplayName("실패: 사용자 이름 수정 - 빈 이름")
    void updateName_emptyName_fail() throws Exception {
        long userId = 1L;
        mockMvc.perform(patch("/user/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("name", ""))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_NAME.getCode()));
    }

    @Test
    @DisplayName("사용자 탈퇴 API 테스트")
    void retireUser() throws Exception {
        // given
        long userId = 1L;
        String name = "user1";
        User retiredUser = User.of(userId, name).withStatus(UserStatus.RETIRED);
        given(userService.retireUser(userId)).willReturn(retiredUser);

        // when & then
        mockMvc.perform(delete("/user/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.status").value(UserStatus.RETIRED.name()));

        verify(userService).retireUser(userId);
    }
} 