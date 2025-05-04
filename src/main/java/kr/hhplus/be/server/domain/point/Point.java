package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "idx_point_user_id", columnList = "user_id"))
public class Point extends BaseTimeEntity {

    private static final long MAX_POINT = 1000000L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long userId;
    private long point;
    /*
    * 트랜잭션 시작 시: DB에서 version=1 읽어옴
    * 변경 후 저장할 때: WHERE id=xxx AND version=1
    * 근데 만약 다른 트랜잭션이 먼저 version=2로 업데이트하면?
    * 나중에 저장하려는 트랜잭션은 업데이트 실패(OptimisticLockException) 가 뜸.
    * .save()만 호출하면 낙관적 락이 자동 적용
    * */
    @Version   // @Version => 낙관적 락 버전 관리 필드
    private long version;

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
