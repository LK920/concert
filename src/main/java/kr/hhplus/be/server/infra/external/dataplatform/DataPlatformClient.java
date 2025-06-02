package kr.hhplus.be.server.infra.external.dataplatform;

import kr.hhplus.be.server.domain.reservation.events.ReservationCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataPlatformClient {

    public void sendReservation(ReservationCompletedEvent event) {
        log.info("[Mock 전송] 데이터 플랫폼 전송 시작 - reservationId: {}", event.reservationId());

        // mock 처리 (실제로는 WebClient, kafka 등 사용)
        log.info("전송 완료 - userId: {}, concertId: {} seatId: {}, reservationId: {}",
                event.userId(), event.concertId(), event.seatId(), event.reservationId());
    }

}
