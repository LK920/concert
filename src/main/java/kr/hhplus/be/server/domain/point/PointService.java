package kr.hhplus.be.server.domain.point;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.support.enums.LockType;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    @Cacheable(value = "userPoint", key = "#userId")
    @Transactional(readOnly = true)
    public PointInfo getUserPoint(long userId){
        Point point = pointRepository.findByUserId(userId).orElseThrow(()->new EntityNotFoundException("해당 유저가 없습니다."));
        return PointInfo.from(point);
    }

    @CacheEvict(value = "userPoint", key = "#userId")
    @Transactional
    public PointInfo chargeUserPoint(long userId, long amount){
        // 포인트 데이터
        Point point = pointRepository.findByUserId(userId).orElseThrow(()-> new EntityNotFoundException("해당 유저가 없습니다."));
        point.chargePoint(amount);
        Point saved = pointRepository.save(point);
        return PointInfo.from(saved);
    }

    @CacheEvict(value = "userPoint", key = "#userId")
    @Transactional
    public void useUserPoint(long userId, long amount){
        // 포인트 데이터
        Point point = pointRepository.findByUserId(userId).orElseThrow(()-> new EntityNotFoundException("해당 유저가 없습니다."));
        point.usePoint(amount);
        pointRepository.save(point);
    }

    // db lock
    private Point findUserPointWithLock(long userId, LockType lockType){
        switch (lockType){
            case OPTIMISTIC -> {
                return pointRepository.findByUserIdLock(userId).orElseThrow(()->new EntityNotFoundException("해당 유저가 없습니다."));
            }
            case PESSIMISTIC -> {
                return pointRepository.findByUserIdForUpdate(userId).orElseThrow(()->new EntityNotFoundException("해당 유저가 없습니다."));
            }
            default -> throw new IllegalIdentifierException("유효하지 않은 lock입니다.");
        }

    }

}
