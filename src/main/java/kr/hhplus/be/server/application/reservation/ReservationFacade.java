package kr.hhplus.be.server.application.reservation;

import kr.hhplus.be.server.domain.ranking.RankingService;
import kr.hhplus.be.server.domain.reservation.ReservationInfo;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.support.redis.RedisLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationFacade {

    private final ReservationService reservationService;
    private final SeatService seatService;
    private final RedisLock redisLock;
    private final RankingService rankingService;

    public ReservationInfo reserveConcert(ReserveConcertCommand reserveConcertCommand) {
        String lockKey = "lock:seat:" + reserveConcertCommand.seatId();
        String lockValue = "lock:value:" + UUID.randomUUID().toString();
        boolean getLock = redisLock.tryLock(lockKey, lockValue);

        if (!getLock) throw new IllegalArgumentException("락 획득 실패했습니다.");

        log.info("락 획득을 성공했습니다.");

        try {
            // 좌석 예약
            seatService.reserveSeat(reserveConcertCommand.seatId());
            // 예약 생성
            ReservationInfo reservationInfo = reservationService.createReservation(
                    reserveConcertCommand.userId(),
                    reserveConcertCommand.concertId(),
                    reserveConcertCommand.seatId(),
                    reserveConcertCommand.seatPrice());
            // 랭킹 포인트 추가
            rankingService.addConcertRankingScore(reserveConcertCommand.concertId());

            return reservationInfo;

        }catch (IllegalArgumentException illegalArgumentException){
            // 좌석 중복 예외
            log.error(illegalArgumentException.getMessage());
            throw new IllegalArgumentException(illegalArgumentException.getMessage(), illegalArgumentException);
        }catch (Exception e){
            log.error("예외 메시지: {}", e.getMessage(), e);
            throw new RuntimeException("예외 발생", e);
        } finally {
            redisLock.unLock(lockKey, lockValue);
            log.info("락을 해제합니다.");
        }
    }

}
