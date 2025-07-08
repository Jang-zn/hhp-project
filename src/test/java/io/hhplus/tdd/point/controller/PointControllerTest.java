package io.hhplus.tdd.point.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureWebMvc
@DisplayName("PointController 통합 테스트")
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("포인트 조회 API 테스트")
    void getPoint() throws Exception {
        // given

        // when & then
    }

    @Test
    @DisplayName("포인트 충전 API 테스트")
    void chargePoint() throws Exception {
        // given & when & then
    }

    @Test
    @DisplayName("포인트 사용 API 테스트")
    void usePoint() throws Exception {
        // given & when & then
    }

    @Test
    @DisplayName("포인트 이력 조회 API 테스트")
    void getPointHistories() throws Exception {
        // given & when & then
    }
} 