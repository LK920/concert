package kr.hhplus.be.server.domain.payment;


import kr.hhplus.be.server.infra.payment.PaymentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentRepositoryCustom {

}
