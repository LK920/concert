package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.domain.events.ReservationCreatedEvent;
import kr.hhplus.be.server.domain.point.eventHandler.PointEventListener;
import kr.hhplus.be.server.infra.event.DomainEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class ReservationServiceIntegrationTest {
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationRepository reservationRepository;

    @MockitoSpyBean
    private PointEventListener pointEventListener; // 실 리스너 감시


    @BeforeEach
    void setUp(){
        reservationRepository.deleteAll();
    }

    @Test
    @DisplayName("예약_생성_성공")
    void createReservation() {
        long seatId = 1l;
        long userId = 1l;
        long concertId = 1l;
        long seatPrice = 1000l;

        ReservationInfo result = reservationService.createReservation(userId, concertId, seatId, seatPrice);

        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.seatId()).isEqualTo(seatId);
        assertThat(result.reservationStatus()).isEqualTo(ReservationStatus.PENDING);

        verify(pointEventListener, timeout(2000)).handleReservationCompleted(any(ReservationCreatedEvent.class));

    }

    @Test
    @DisplayName("예약_취소_실패_이미_취소")
    void cancelReservation_fail_alreadyCanceled() {
        Reservation reservation = Reservation.create(1l, 1l);
        reservation.cancelReservation();
        Reservation saved = reservationRepository.save(reservation);

        assertThatThrownBy(()->reservationService.cancelReservation(saved.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 취소된 예약입니다.");
    }

    @Test
    @DisplayName("예약_취소_성공")
    void cancelReservation_success() {
        Reservation reservation = Reservation.create(1l, 1l);
        Reservation saved = reservationRepository.save(reservation);

        ReservationInfo result = reservationService.cancelReservation(saved.getId());

        assertThat(result.reservationStatus()).isEqualTo(ReservationStatus.CANCELED);
    }

    @Test
    @DisplayName("예약_내역_결제_내역_추가_성공")
    void updatePaymentInfo_success() {
        ReservationInfo info = reservationService.createReservation(1l, 1l, 1l, 1000l);
        long paymentId = 1l;

        ReservationInfo result = reservationService.updatePaymentInfo(info.reservationId(), paymentId);

        assertThat(result.userId()).isEqualTo(info.userId());
        assertThat(result.reservationId()).isEqualTo(info.reservationId());
        assertThat(result.reservationStatus()).isEqualTo(ReservationStatus.COMPLETE);
    }

    @Test
    @DisplayName("예약_내역_조회_성공_빈값")
    void getUserReservationInfo_success_empty() {
        List<ReservationInfo> info = reservationService.getUserReservationInfo(1l);

        assertThat(info).isEmpty();
    }

    @Test
    @DisplayName("예약_내역_조회_성공")
    void getUserReservationInfo_success() {

        Reservation r1 = Reservation.create(1, 1);
        Reservation r2 = Reservation.create(2, 1);
        r2.addPaymentId(1l);
        r2.completeReservation();
        Reservation r3 = Reservation.create(3, 2);
        Reservation r4 = Reservation.create(4, 1);
        r4.cancelReservation();

        reservationRepository.saveAll(List.of(r1,r2,r3,r4));

        List<ReservationInfo> result = reservationService.getUserReservationInfo(1l);

        assertThat(result).hasSize(3); // userId = 1인 예약이 3건

        // 예약 ID와 상태를 확인
        assertThat(result).extracting("reservationId")
                .containsExactlyInAnyOrder(r1.getId(), r2.getId(), r4.getId());

        assertThat(result).filteredOn(r -> r.reservationId() == r1.getId())
                .extracting("reservationStatus").containsExactly(ReservationStatus.PENDING);
        assertThat(result).filteredOn(r -> r.reservationId() == r2.getId())
                .extracting("reservationStatus").containsExactly(ReservationStatus.COMPLETE);
        assertThat(result).filteredOn(r -> r.reservationId() == r4.getId())
                .extracting("reservationStatus").containsExactly(ReservationStatus.CANCELED);

    }
}