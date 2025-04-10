package kr.hhplus.be.server.domain.point;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point extends BaseTimeEntity {

    private static final long MAX_POINT = 1000000L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long userId;
    private long point;

    private Point(long userId, long point){
        this.userId = userId;
        this.point = point;
    }

    public static Point create(long userId, long point){
        return new Point(userId, point);
    }

    public void usePoint(long amount){
        if(amount <= 0){throw new IllegalArgumentException("사용할 포인트는 0보다 커야합니다.");}
        if(amount > this.getPoint()){throw new IllegalArgumentException("포인트가 부족합니다.");}
        this.point -= amount;
    }

    public void chargePoint(long amount){
        if(amount <= 0){throw new IllegalArgumentException("충전 포인트는 0보다 커야합니다.");}
        long newPoint = this.point + amount;
        if(newPoint>MAX_POINT){throw new IllegalArgumentException("최대 잔고를 초과할 수 없습니다.");}
        this.point += amount;
    }

}
