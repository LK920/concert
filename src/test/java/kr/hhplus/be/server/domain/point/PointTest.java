package kr.hhplus.be.server.domain.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointTest {

    @Test
    @DisplayName("포인트 생성 성공")
    void createPoint(){
        Point point = Point.create(1, 20);
        assertThat(point.getPoint()).isEqualTo(20);
        assertThat(point.getUserId()).isEqualTo(1);
    }

    @Test
    @DisplayName("충전 포인트 0이하 예외")
    void chargePointException(){
        long amount = 0;
        Point point = Point.create(1, 40);
        assertThatThrownBy(()->point.chargePoint(amount))
                .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("충전 포인트는 0보다 커야합니다.");
    }

    @Test
    @DisplayName("충전 포인트 최대 잔고 초과")
    void chargePoint_exceed_max_point(){
        long amount = 10000000L;
        Point point = Point.create(1, 40);
        assertThatThrownBy(()->point.chargePoint(amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("최대 잔고를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("사용 포인트 0이하 예외")
    void usePointException(){
        long amount = 0;
        Point point = Point.create(1, 40);
        assertThatThrownBy(()->point.usePoint(amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용할 포인트는 0보다 커야합니다.");
    }

    @Test
    @DisplayName("포인트 부족 예외")
    void insufficientPointException(){
        long amount = 501;
        Point point = Point.create(1, 500);
        assertThatThrownBy(()->point.usePoint(amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("포인트가 부족합니다.");
    }

}