package kr.hhplus.be.server.domain.concert;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;

    public Page<ConcertInfo> getConcerts(Pageable pageable){
        Page<Concert> concertList = concertRepository.findAllConcerts(pageable);

        return concertList.map(
                concert->ConcertInfo.from(concert)
        );
    }
}
