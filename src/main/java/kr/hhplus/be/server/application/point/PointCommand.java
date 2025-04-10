package kr.hhplus.be.server.application.point;

public record PointCommand(
        long userId,
        long userPoint
) {
}
