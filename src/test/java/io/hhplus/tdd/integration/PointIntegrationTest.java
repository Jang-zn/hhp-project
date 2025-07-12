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
@DisplayName("포인트 API 통합 테스트")
class PointIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private long createUser(String name) throws Exception {
        //given: 유저 생성 요청 보냄
        String res = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"" + name + "\"}"))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        //then: 생성된 유저 id 반환함
        return objectMapper.readTree(res).get("id").asLong();
    }

    @Test
    @DisplayName("성공: 포인트 충전/사용/조회/이력")
    void pointDomain_integration_success() throws Exception {
        //given: 유저 생성하고, 충전/사용 금액 준비함
        long userId = createUser("TestUser");
        long charge = 1000L;
        long use = 400L;

        //when: 포인트 충전 요청
        mockMvc.perform(patch("/point/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(charge)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId))
            .andExpect(jsonPath("$.point").value(charge));

        //when: 포인트 사용 요청
        mockMvc.perform(patch("/point/{id}/use", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(use)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId))
            .andExpect(jsonPath("$.point").value(charge - use));

        //when: 포인트 조회 요청
        mockMvc.perform(get("/point/{id}", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId))
            .andExpect(jsonPath("$.point").value(charge - use));

        //when: 포인트 이력 조회 요청
        mockMvc.perform(get("/point/{id}/histories", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].userId").value(userId))
            .andExpect(jsonPath("$[1].userId").value(userId));
    }

    @Test
    @DisplayName("실패케이스: 없는 유저 포인트 충전/사용/조회/이력")
    void pointDomain_userNotFound_fail() throws Exception {
        long notExistUserId = 99999L;
        mockMvc.perform(patch("/point/{id}/charge", notExistUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(100)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()));
        mockMvc.perform(patch("/point/{id}/use", notExistUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(100)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()));
        mockMvc.perform(get("/point/{id}", notExistUserId))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()));
        mockMvc.perform(get("/point/{id}/histories", notExistUserId))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("실패케이스: 포인트 충전 - 최대치 초과")
    void chargePoint_overMax_fail() throws Exception {
        long userId = createUser("MaxAmountUser");
        long amount = Long.MAX_VALUE;
        mockMvc.perform(patch("/point/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(amount)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.MAX_POINT_LIMIT_EXCEEDED.getCode()))
            .andExpect(jsonPath("$.message").value(ErrorCode.MAX_POINT_LIMIT_EXCEEDED.getMessage()));
    }

    @Test
    @DisplayName("실패케이스: 포인트 충전 - 음수 금액")
    void chargePoint_negativeAmount_fail() throws Exception {
        long userId = createUser("NegativeAmountUser");
        long amount = -100L;
        mockMvc.perform(patch("/point/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(amount)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_AMOUNT.getCode()))
            .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_AMOUNT.getMessage()));
    }

    @Test
    @DisplayName("실패케이스: 포인트 사용 - 잔액 부족")
    void usePoint_notEnough_fail() throws Exception {
        long userId = createUser("NotEnoughPointUser");
        long charge = 100L;
        long use = 200L;
        mockMvc.perform(patch("/point/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(charge)))
            .andExpect(status().isOk());
        mockMvc.perform(patch("/point/{id}/use", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(use)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.INSUFFICIENT_POINT.getCode()))
            .andExpect(jsonPath("$.message").value(ErrorCode.INSUFFICIENT_POINT.getMessage()));
    }

    @Test
    @DisplayName("실패케이스: 포인트 사용 - 음수 금액")
    void usePoint_negativeAmount_fail() throws Exception {
        long userId = createUser("NegativeUseAmountUser");
        long charge = 1000L;
        long use = -100L;
        mockMvc.perform(patch("/point/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(charge)))
            .andExpect(status().isOk());
        mockMvc.perform(patch("/point/{id}/use", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(use)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_AMOUNT.getCode()))
            .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_AMOUNT.getMessage()));
    }

    @Test
    @DisplayName("성공: 포인트 이력 - 여러 번 충전/사용")
    void pointHistory_multiple_success() throws Exception {
        long userId = createUser("User");
        mockMvc.perform(patch("/point/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(100)))
            .andExpect(status().isOk());
        mockMvc.perform(patch("/point/{id}/use", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(50)))
            .andExpect(status().isOk());
        mockMvc.perform(patch("/point/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(200)))
            .andExpect(status().isOk());
        mockMvc.perform(patch("/point/{id}/use", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(100)))
            .andExpect(status().isOk());
        mockMvc.perform(get("/point/{id}/histories", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].userId").value(userId))
            .andExpect(jsonPath("$[1].userId").value(userId))
            .andExpect(jsonPath("$[2].userId").value(userId))
            .andExpect(jsonPath("$[3].userId").value(userId));
    }
} 