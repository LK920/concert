package kr.hhplus.be.server.domain.queue;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WaitingQueue extends BaseTimeEntity {
    private static final long EXPIRED_TIME = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long userId;
    private String token;
    @Enumerated
    private WaitingQueueStatus status;
    private LocalDateTime expiredAt;

    private WaitingQueue(String token, long userId, LocalDateTime now){
        this.token = token;
        this.userId = userId;
        this.status = WaitingQueueStatus.WAITING;
        this.expiredAt = now.plusMinutes(EXPIRED_TIME);
    }

    public static WaitingQueue create(String token, long userId, LocalDateTime now){
        return new WaitingQueue(token, userId,now);
    }

    public void isActived(){
        if(this.status != WaitingQueueStatus.WAITING){throw new IllegalArgumentException("대기 상태가 아닙니다.");}
        this.status = WaitingQueueStatus.ACTIVE;
    }

    public void isExpired(){
        if(this.status == WaitingQueueStatus.EXPIRED){throw new IllegalArgumentException("이미 만료된 상태입니다.");}
        this.status = WaitingQueueStatus.EXPIRED;
    }

}
