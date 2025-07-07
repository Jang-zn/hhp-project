package io.hhplus.tdd.integration;

import io.hhplus.tdd.point.model.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@DisplayName("포인트 시스템 통합 테스트")
class PointIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private PointService pointService;
    
    @Test
    @DisplayName("포인트 충전 후 조회 시나리오 테스트")
    void chargeAndGetPointScenario() throws Exception {
        // given & when & then
        // TODO: 포인트 충전 -> 조회 시나리오 테스트
    }
    
    @Test
    @DisplayName("포인트 충전 후 사용 시나리오 테스트")
    void chargeAndUsePointScenario() throws Exception {
        // given & when & then
        // TODO: 포인트 충전 -> 사용 시나리오 테스트
    }
    
    @Test
    @DisplayName("포인트 이력 추적 시나리오 테스트")
    void pointHistoryTrackingScenario() throws Exception {
        // given & when & then
        // TODO: 포인트 이력 추적 시나리오 테스트
    }
} 