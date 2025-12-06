package com.shoppingmall.project_shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelRequestDto {

    private String orderUid;   // 어떤 주문인지
    private String reason;     // USER_CLICK_CANCEL_BUTTON 등 (로그용)
}