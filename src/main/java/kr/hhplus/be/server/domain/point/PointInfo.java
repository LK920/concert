package kr.hhplus.be.server.domain.point;

public record PointInfo(
        long userId,
        long userPoint
) {
    public static PointInfo from(Point point){
        return new PointInfo(point.getUserId(), point.getPoint());
    }
}
