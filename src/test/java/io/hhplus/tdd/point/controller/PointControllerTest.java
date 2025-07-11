package io.hhplus.tdd.point.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.common.constants.TransactionType;
import io.hhplus.tdd.point.model.PointHistory;
import io.hhplus.tdd.point.model.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import io.hhplus.tdd.common.constants.ErrorCode;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import static org.assertj.core.api.Assertions.assertThat;
import java.lang.reflect.Field;


@WebMvcTest(controllers = PointController.class)
@DisplayName("PointController 단위 테스트")
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PointService pointService;

    @Autowired
    private PointController pointController;

    @BeforeEach 
    void resetRateLimitMap() throws Exception {
        Field field = PointController.class.getDeclaredField("rateLimitMap");
        field.setAccessible(true);
        ((java.util.concurrent.ConcurrentHashMap<?, ?>) field.get(pointController)).clear();
    }

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

    @ParameterizedTest
    @ValueSource(longs = {0L, -100L})
    @DisplayName("실패: 포인트 충전 - 잘못된 금액(음수/0)")
    void chargePoint_invalidAmount_fail(Long amountToCharge) throws Exception {
        long userId = 1L;
        mockMvc.perform(patch("/point/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(amountToCharge)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_AMOUNT.getCode()));
    }

    @Test
    @DisplayName("실패: 포인트 충전 - null 금액")
    void chargePoint_nullAmount_fail() throws Exception {
        long userId = 1L;
        mockMvc.perform(patch("/point/{id}/charge", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.message").value("요청 파라미터 형식이 잘못되었습니다."));
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

    @ParameterizedTest
    @ValueSource(longs = {0L, -100L})
    @DisplayName("실패: 포인트 사용 - 잘못된 금액(음수/0)")
    void usePoint_invalidAmount_fail(Long amountToUse) throws Exception {
        long userId = 1L;
        mockMvc.perform(patch("/point/{id}/use", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(amountToUse)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_AMOUNT.getCode()));
    }

    @Test
    @DisplayName("실패: 포인트 사용 - null 금액")
    void usePoint_nullAmount_fail() throws Exception {
        long userId = 1L;
        mockMvc.perform(patch("/point/{id}/use", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.message").value("요청 파라미터 형식이 잘못되었습니다."));
    }

    @Test
    @DisplayName("성공: 유저의 포인트 이력을 정상적으로 조회한다")
    void getPointHistoryList() throws Exception {
        // given
        long userId = 1L;
        List<PointHistory> expectedHistories = List.of(
                new PointHistory(1L, userId, 1000, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(2L, userId, 200, TransactionType.USE, System.currentTimeMillis())
        );
        given(pointService.getPointHistoryList(userId)).willReturn(expectedHistories);

        // when & then
        mockMvc.perform(get("/point/{id}/histories", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].type").value(TransactionType.CHARGE.name()));

        verify(pointService).getPointHistoryList(userId);
    }

    @Test
    @DisplayName("성공(엣지케이스): 포인트 이력이 없는 유저 조회 시 빈 배열을 반환한다")
    void getPointHistoryList_whenNoHistories() throws Exception {
        // given
        long userId = 2L;
        given(pointService.getPointHistoryList(anyLong())).willReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/point/{id}/histories", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(pointService).getPointHistoryList(userId);
    }

    @Test
    @DisplayName("실패: 1초 내 같은 userId 5회 초과 충전 요청 시 429 Too Many Requests 반환, 다른 userId는 정상 처리")
    void concurrentChargePoint() throws Exception {
        // given: 동시 요청을 보낼 스레드풀, 동기화용 latch, 결과 수집용 리스트 준비
        int thread1Count = 10;
        int thread2Count = 5;
        List<Long> userIds = new ArrayList<>();
        for (int i = 0; i < thread1Count; i++) {
            userIds.add(1L);
        }
        for (int i = 0; i < thread2Count; i++) {
            userIds.add(2L);
        }
        int totalRequests = userIds.size();
        ExecutorService executor = Executors.newFixedThreadPool(totalRequests);
        CountDownLatch readyLatch = new CountDownLatch(totalRequests);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(totalRequests);
        List<Future<MvcResult>> results = new ArrayList<>();
        for (Long userId : userIds) {
            results.add(executor.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    return mockMvc.perform(patch("/point/" + userId + "/charge")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("100"))
                        .andReturn();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    doneLatch.countDown();
                }
            }));
        }
        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        // then: 결과를 userId별로 분류해서 429/200 개수 검증
        int user1_429 = 0, user2_429 = 0, user2_200 = 0;
        for (int i = 0; i < results.size(); i++) {
            Long userId = userIds.get(i);
            MvcResult result = results.get(i).get();
            int status = result.getResponse().getStatus();
            if (userId == 1L) {
                if (status == 429) {
                    user1_429++;
                    // 응답을 UTF-8로 변환 안하니까 한글 다 깨지는 현상 발생
                    String content = new String(result.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
                    JsonNode json = objectMapper.readTree(content);
                    assertThat(json.get("code").asText()).isEqualTo(ErrorCode.TOO_MANY_REQUESTS.getCode());
                    assertThat(json.get("message").asText()).isEqualTo(ErrorCode.TOO_MANY_REQUESTS.getMessage());
                }
            } else if (userId == 2L) {
                if (status == 200) {
                    user2_200++;
                } else if (status == 429) {
                    user2_429++;
                }
            }
        }
        // userId=1L: 429가 1개 이상, 200도 일부 있을 수 있음
        assertThat(user1_429).isGreaterThan(0);
        // userId=2L: 모두 200, 429는 없어야 함
        assertThat(user2_200).isEqualTo(5);
        assertThat(user2_429).isEqualTo(0);
    }
} 