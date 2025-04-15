package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long userId;
    private long amount;
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private Payment(long userId, long amount, PaymentType paymentType){
        if(amount <= 0){throw new IllegalArgumentException("결제 금액은 0보다 커야합니다.");}
        if(paymentType == null){throw new IllegalArgumentException("결제 타입은 필수입니다.");}

        this.userId = userId;
        this.amount = amount;
        this.paymentType = paymentType;
    }

    public static Payment create(long userId, long amount, PaymentType paymentType){
        return new Payment(userId,amount, paymentType);
    }

}
