package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.service.PointService;
import io.hhplus.tdd.point.model.UserPoint;
import io.hhplus.tdd.point.model.PointHistory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PointController.class)
@DisplayName("PointController 웹 레이어 테스트")
class PointControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private PointService pointService;
    
    @Test
    @DisplayName("포인트 조회 API 테스트")
    void getPoint() throws Exception {
        // given
        long userId = 1L;
        UserPoint userPoint = new UserPoint(userId, 1000L, System.currentTimeMillis());
        given(pointService.getPoint(userId)).willReturn(userPoint);
        
        // when & then
        mockMvc.perform(get("/point/{id}", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId))
            .andExpect(jsonPath("$.point").value(1000L));
    }
    
    @Test
    @DisplayName("포인트 충전 API 테스트")
    void chargePoint() throws Exception {
        // given & when & then
        // TODO: 포인트 충전 API 테스트
    }
    
    @Test
    @DisplayName("포인트 사용 API 테스트")
    void usePoint() throws Exception {
        // given & when & then
        // TODO: 포인트 사용 API 테스트
    }
    
    @Test
    @DisplayName("포인트 이력 조회 API 테스트")
    void getPointHistories() throws Exception {
        // given & when & then
        // TODO: 포인트 이력 조회 API 테스트
    }
} 