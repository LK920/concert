package kr.hhplus.be.server.domain.seat;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

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

}
