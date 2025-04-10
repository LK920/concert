package kr.hhplus.be.server.domain.reservation;

public record ReservationInfo(
        long reservationId,
        long userId,
        long seatId,
        ReservationStatus reservationStatus,
        Long paymentId
) {

    public static ReservationInfo from(Reservation reservation){
        return new ReservationInfo(reservation.getId(), reservation.getUserId(), reservation.getConcertSeatId(), reservation.getReservationStatus(), reservation.getPaymentId());
    }

}
