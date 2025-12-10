package com.shoppingmall.project_shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFailRequestDto {

    private String orderUid;    // 우리 쪽 주문번호(merchant_uid)
    private String impUid;      // PG 결제번호(있으면)
    private Integer paidAmount; // PG가 전달한 금액 (보통 0)
    private String payMethod;   // CARD / VBANK / 등 (없을 수도)
    private String failCode;    // PG 에러코드
    private String failReason;  // PG 에러메시지
}
