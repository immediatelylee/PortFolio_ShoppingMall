package com.shoppingmall.project_shoppingmall.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PaymentAmounts {

    // 상품 합계 (order.getTotalPrice())
    private final BigDecimal itemsTotal;

    // 배송비 (원 단위, 0 또는 2500)
    private final int deliveryFee;

    // 상품 + 배송비
    private final BigDecimal totalPay;

    public PaymentAmounts(BigDecimal itemsTotal, int deliveryFee) {
        this.itemsTotal = itemsTotal != null ? itemsTotal : BigDecimal.ZERO;
        this.deliveryFee = deliveryFee;
        this.totalPay = this.itemsTotal.add(BigDecimal.valueOf(deliveryFee));
    }
}
