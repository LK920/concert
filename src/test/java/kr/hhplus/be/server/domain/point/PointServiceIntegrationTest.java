package kr.hhplus.be.server.domain.point;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import(TestcontainersConfiguration.class)
public class PointServiceIntegrationTest {
    @Autowired
    private PointService pointService;
    @Autowired
    private PointRepository pointRepository;
    @BeforeEach
    void setUp(){
        pointRepository.deleteAll();

        Point p1 = Point.create(1l, 2000);
        Point p2 = Point.create(2l, 1000);
        Point p3 = Point.create(3l, 10000);
        pointRepository.saveAll(List.of(p1,p2,p3));
    }

    @Test
    @DisplayName("유저_포인트_조회")
    void getUserPoint_success() {
        long userId = 1l;

        PointInfo result = pointService.getUserPoint(userId);

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.userPoint()).isEqualTo(2000);
    }
    @Test
    @DisplayName("유저_포인트_조회_없는_유저_예외")
    void getUserPoint_fail_whenNoUser() {
        long userId = 999l;

        assertThatThrownBy(()->pointService.getUserPoint(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 유저가 없습니다.");
    }

    @Test
    @DisplayName("유저_포인트_충전_존재하지않는_유저_예외")
    void chargeUserPoint_fail_NoUser() {
        long unknownId = 999l;
        long amount = 2000l;
        assertThatThrownBy(()->pointService.chargeUserPoint(unknownId, amount))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 유저가 없습니다.");
    }

    @Test
    @DisplayName("포인트_충전_실패_음수입력")
    void chargeUserPoint_fail_negativeAmount() {
        assertThatThrownBy(() -> pointService.chargeUserPoint(1L, -500))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("충전 포인트는 0보다 커야합니다.");
    }

    @Test
    @DisplayName("포인트_충전_실패_최대_포인트_초과")
    void chargeUserPoint_fail_maxAmount() {
        assertThatThrownBy(() -> pointService.chargeUserPoint(3L, 999000L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("최대 잔고를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("유저_포인트_충전")
    void chargeUserPoint_success() {
        long userId = 1l;
        long amount = 2000l;
        Optional<Point> origin = pointRepository.findByUserId(userId);
        origin.get().chargePoint(2000l);
        PointInfo result = pointService.chargeUserPoint(userId,amount);

        assertThat(result).isNotNull();
        assertThat(result.userPoint()).isEqualTo(origin.get().getPoint());
        assertThat(result.userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("유저_포인트_사용_없는_유저_예외")
    void useUserPoint_fail_noUser() {
        long unknownId = 999l;
        long amount = 1000l;

        assertThatThrownBy(()->pointService.useUserPoint(unknownId,amount))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 유저가 없습니다.");
    }

    @Test
    @DisplayName("포인트_사용_실패_잔액부족")
    void useUserPoint_fail_insufficient() {
        assertThatThrownBy(() -> pointService.useUserPoint(2L, 2000L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("포인트가 부족합니다.");
    }

    @Test
    @DisplayName("포인트_사용_실패_음수입력")
    void useUserPoint_fail_negative() {
        assertThatThrownBy(() -> pointService.useUserPoint(1L, -100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용할 포인트는 0보다 커야합니다.");
    }

    @Test
    @DisplayName("유저_포인트_사용")
    void useUserPoint_success() {
        long userId = 1l;
        long amount = 1000l;
        Optional<Point> origin = pointRepository.findByUserId(userId);
        origin.get().usePoint(amount);

        pointService.useUserPoint(userId,amount);

        Optional<Point> updated = pointRepository.findByUserId(userId);
        assertThat(updated.get().getUserId()).isEqualTo(userId);
        assertThat(updated.get().getPoint()).isEqualTo(origin.get().getPoint());

    }
}
