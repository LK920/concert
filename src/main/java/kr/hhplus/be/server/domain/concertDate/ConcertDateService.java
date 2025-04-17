package kr.hhplus.be.server.domain.concertDate;

import kr.hhplus.be.server.domain.concert.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertDateService {

    private final ConcertDateRepository concertDateRepository;

    @Transactional(readOnly = true)
    public List<ConcertDateInfo> getConcertAvailableDates(long concertId) {
        List<ConcertDate> concertDateList = concertDateRepository.findAllByConcertIdAndIsAvailableTrue(concertId);
        List<ConcertDateInfo> result = concertDateList.stream()
                .map(concertDate -> ConcertDateInfo.from(concertDate))
                .toList();
        return result;
    }
}
