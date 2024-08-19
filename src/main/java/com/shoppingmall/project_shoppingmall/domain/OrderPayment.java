package com.shoppingmall.project_shoppingmall.domain;

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

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public OrderPayment(String paymentMethod, BigDecimal amount, LocalDateTime paymentDate) {
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }
}
