package kr.hhplus.be.server.application.concert;

import kr.hhplus.be.server.domain.concert.ConcertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class concertFacade {

    @Autowired
    private ConcertService concertService;

}
