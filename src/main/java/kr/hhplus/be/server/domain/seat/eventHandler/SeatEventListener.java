package kr.hhplus.be.server.domain.seat.eventHandler;

import kr.hhplus.be.server.domain.events.ReservationFailedEvent;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.infra.external.DataPlatformClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatEventListener {
    private final SeatService seatService;
    private final DataPlatformClient dataPlatformClient;

    @Async
    @EventListener
    public void handleSeatCanceled(ReservationFailedEvent event){
        seatService.cancelSeat(event.SeatId());
        log.warn("[좌석 취소] seatId:{}", event.SeatId());
    }

}
