package com.shoppingmall.project_shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompleteRequestDto {
    //iamport에서 쓰는 값들
    private String orderUid;    // merchant_uid로 쓰는 우리 주문번호
    private String impUid;
    private String pgTid;
    private Integer paidAmount;
}
