package kr.hhplus.be.server.domain.concert;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConcertService {

    private Concert concert;

    private ConcertRepository concertRepository;

    @Transactional(readOnly = true)
    public List<ConcertInfo> getConcerts(){
        List<Concert> concertList = concertRepository.findAll();

        return concertList.stream().map(
                concert->ConcertInfo.from(concert)
        ).toList();
    }
}
