package kr.hhplus.be.server.domain.seat;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<SeatInfo> getAvailableSeats(long concertDateId){
        List<Seat> seatList = seatRepository.findByConcertDateIdAndSeatStatus(concertDateId, SeatStatus.ENABLE);
        return seatList.stream().map(
                seat -> SeatInfo.from(seat)
        ).toList();
    }
    @Transactional
    public void reserveSeat(long seatId){
        Seat seat = seatRepository.findBySeatId(seatId);
        seat.reserveSeat();
        seatRepository.save(seat);
    }

    @Transactional
    public void cancelSeat(long seatId){
        Seat seat = seatRepository.findBySeatId(seatId);
        seat.cancelSeatReservation();
        seatRepository.save(seat);
    }

}
