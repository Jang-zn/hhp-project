package io.hhplus.tdd.point.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.common.constants.TransactionType;
import io.hhplus.tdd.point.model.PointHistory;
import io.hhplus.tdd.point.model.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = PointController.class)
@DisplayName("PointController 단위 테스트")
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PointService pointService;

    @Test
    @DisplayName("성공: 유저의 포인트를 정상적으로 조회한다")
    void getPoint() throws Exception {
        // given
        long userId = 1L;
        long amount = 1000L;
        UserPoint expectedUserPoint = new UserPoint(userId, amount, System.currentTimeMillis());
        given(pointService.getPoint(userId)).willReturn(expectedUserPoint);

        // when & then
        mockMvc.perform(get("/point/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(amount));

        verify(pointService).getPoint(userId);
    }

    @Test
    @DisplayName("성공: 포인트를 정상적으로 충전한다")
    void chargePoint() throws Exception {
        // given
        long userId = 1L;
        long amountToCharge = 500L;
        UserPoint chargedUserPoint = new UserPoint(userId, 1500L, System.currentTimeMillis());
        given(pointService.charge(userId, amountToCharge)).willReturn(chargedUserPoint);

        // when & then
        mockMvc.perform(patch("/point/{id}/charge", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amountToCharge)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(chargedUserPoint.point()));

        verify(pointService).charge(userId, amountToCharge);
    }

    @Test
    @DisplayName("성공: 포인트를 정상적으로 사용한다")
    void usePoint() throws Exception {
        // given
        long userId = 1L;
        long amountToUse = 300L;
        UserPoint usedUserPoint = new UserPoint(userId, 700L, System.currentTimeMillis());
        given(pointService.use(userId, amountToUse)).willReturn(usedUserPoint);

        // when & then
        mockMvc.perform(patch("/point/{id}/use", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amountToUse)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(usedUserPoint.point()));

        verify(pointService).use(userId, amountToUse);
    }

    @Test
    @DisplayName("성공: 유저의 포인트 이력을 정상적으로 조회한다")
    void getPointHistories() throws Exception {
        // given
        long userId = 1L;
        List<PointHistory> expectedHistories = List.of(
                new PointHistory(1L, userId, 1000, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(2L, userId, 200, TransactionType.USE, System.currentTimeMillis())
        );
        given(pointService.getPointHistories(userId)).willReturn(expectedHistories);

        // when & then
        mockMvc.perform(get("/point/{id}/history", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].type").value(TransactionType.CHARGE.name()));

        verify(pointService).getPointHistories(userId);
    }

    @Test
    @DisplayName("성공(엣지케이스): 포인트 이력이 없는 유저 조회 시 빈 배열을 반환한다")
    void getPointHistories_whenNoHistories() throws Exception {
        // given
        long userId = 2L;
        given(pointService.getPointHistories(anyLong())).willReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/point/{id}/history", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(pointService).getPointHistories(userId);
    }
} 