package kr.hhplus.be.server.domain;

import kr.hhplus.be.server.domain.events.ReservationCompletedEvent;
import kr.hhplus.be.server.infra.external.DataPlatformClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataPlatformService {

    private final DataPlatformClient dataPlatformClient;

    public void process(ReservationCompletedEvent event){
        try{
            dataPlatformClient.sendReservation(event);
        } catch (Exception e) {
            log.error("❌ 데이터 플랫폼 전송 실패 - reservationId: {}", event.reservationId(), e);
        }
    }
}
