package kr.hhplus.be.server.domain.concert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConcertService {

    private final ConcertRepository concertRepository;

    @Cacheable(value = "concert", key = "'concertList'")
    @Transactional(readOnly = true)
    public List<ConcertInfo> getConcerts(){
        List<Concert> concertList = concertRepository.findAllConcerts();
        return concertList.stream().map(concert -> ConcertInfo.from(concert)).toList();
    }
}
