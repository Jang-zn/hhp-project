package io.hhplus.tdd.point.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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

        // when & then
    }

    @Test
    @DisplayName("성공: 포인트를 정상적으로 충전한다")
    void chargePoint() throws Exception {
        // given & when & then
    }

    @Test
    @DisplayName("성공: 포인트를 정상적으로 사용한다")
    void usePoint() throws Exception {
        // given & when & then
    }

    @Test
    @DisplayName("성공: 유저의 포인트 이력을 정상적으로 조회한다")
    void getPointHistories() throws Exception {
        // given & when & then
    }
} 