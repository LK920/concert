package kr.hhplus.be.server.domain.queue;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.naming.InsufficientResourcesException;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "idx_waiting_queue_user_id", columnList = "user_id"))
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

    private WaitingQueue(String token, long userId){
        this.token = token;
        this.userId = userId;
        this.status = WaitingQueueStatus.WAITING;
        this.expiredAt = null;
    }

    public static WaitingQueue create(String token, long userId){
        return new WaitingQueue(token, userId);
    }

    public boolean isExpired(LocalDateTime now){
        return this.expiredAt != null
                && this.status == WaitingQueueStatus.ACTIVE
                && this.expiredAt.isBefore(now);
    }

    public boolean isActivated(long currentActiveCount, long maxCapacity){
        if(this.status != WaitingQueueStatus.WAITING){return false;}
        if (currentActiveCount >= maxCapacity) {return false;}
        return true;
    }

    public void active(){
        this.status = WaitingQueueStatus.ACTIVE;
        this.expiredAt = LocalDateTime.now().plusMinutes(EXPIRED_TIME);
    }

    public void active(LocalDateTime now){
        this.status = WaitingQueueStatus.ACTIVE;
        this.expiredAt = now.plusMinutes(EXPIRED_TIME);
    }

    public void expire(){
        this.status = WaitingQueueStatus.EXPIRED;
        this.expiredAt = null;
    }

}
