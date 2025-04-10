package kr.hhplus.be.server.domain.point;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    @Transactional(readOnly = true)
    public PointInfo getUserPoint(long userId){
        Point point = pointRepository.findByUserId(userId).orElseThrow(()->new EntityNotFoundException("해당 유저가 없습니다."));
        return PointInfo.from(point);
    }

    @Transactional
    public PointInfo chargeUserPoint(long userId, long amount){
        // 포인트 데이터
        Point point = pointRepository.findByUserId(userId).orElseThrow(()->new EntityNotFoundException("해당 유저가 없습니다."));
        point.chargePoint(amount);
        pointRepository.save(point);
        return PointInfo.from(point);
    }

    @Transactional
    public void useUserPoint(long userId, long amount){
        // 포인트 데이터
        Point point = pointRepository.findByUserId(userId).orElseThrow(()->new EntityNotFoundException("해당 유저가 없습니다."));
        point.usePoint(amount);
        pointRepository.save(point);
    }

}
