package com.shoppingmall.project_shoppingmall.domain;

import com.shoppingmall.project_shoppingmall.constant.PaymentStatus;
import lombok.*;

import javax.persistence.*;
import java.math.*;
import java.time.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrderPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentMethod;
    private BigDecimal amount; // amount 필드를 BigDecimal로 변경
    private LocalDateTime paymentDate;

    private String payKey;       // PG에서 내려주는 키 (있으면)
    private String pgTid;        // PG 거래 ID (선택)

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;



    @Builder
    public OrderPayment(Order order,
                        String paymentMethod,
                        BigDecimal amount,
                        LocalDateTime paymentDate,
                        PaymentStatus paymentStatus,
                        String payKey,
                        String pgTid) {
        this.order = order;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentStatus = paymentStatus;
        this.payKey = payKey;
        this.pgTid = pgTid;
    }

    public void markSuccess() {
        this.paymentStatus = PaymentStatus.SUCCESS;
    }

    public void markFailed() {
        this.paymentStatus = PaymentStatus.FAILED;
    }
}
