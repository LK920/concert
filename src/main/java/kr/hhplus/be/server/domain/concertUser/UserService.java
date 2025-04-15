package kr.hhplus.be.server.domain.concertUser;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ConcertUser getUser(long userId){
        return userRepository.findById(userId);
    }

}
