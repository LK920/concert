package kr.hhplus.be.server.domain.reservation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    @Test
    @DisplayName("이미 취소된 예약 취소 예외")
    void alreadyCancelException(){
        Reservation reservation = Reservation.create(1, 1);
        reservation.cancelReservation();

        assertThatThrownBy(()->reservation.cancelReservation()).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 취소된 예약입니다.");
    }

    @Test
    @DisplayName("완료 또는 취소 예약 완료 예외")
    void completeReservationWhenNotPendingStatus(){
        Reservation reservation = Reservation.create(1l,1l);
        reservation.cancelReservation();
        assertThatThrownBy(()->reservation.completeReservation()).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("대기중인 예약만 완료할 수 있습니다.");
    }

    @Test
    @DisplayName("결제 내역이 없는 예약 완료 예외")
    void completeReservationWithoutPaymentId(){
        Reservation reservation = Reservation.create(1l,1l);

        assertThatThrownBy(()->reservation.completeReservation()).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제 내역이 추가되지 않았습니다.");
    }

    @Test
    @DisplayName("이미 결제된 예약 예외")
    void alreadyPaymentReservation(){
        Reservation reservation = Reservation.create(1l,1l);
        reservation.addPaymentId(1l);
        assertThatThrownBy(()->reservation.addPaymentId(2l)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 결제가 완료된 예약입니다.");
    }

    @Test
    @DisplayName("결제 내역 추가 성공")
    void addPayment_success(){
        Reservation reservation = Reservation.create(1l,1l);
        reservation.addPaymentId(1l);

        assertThat(reservation.getPaymentId()).isEqualTo(1l);

    }


}