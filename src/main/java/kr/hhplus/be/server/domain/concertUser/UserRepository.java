package kr.hhplus.be.server.domain.concertUser;

public interface UserRepository {
    ConcertUser findById(long userId);
}
