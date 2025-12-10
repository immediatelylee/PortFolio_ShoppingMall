package com.shoppingmall.project_shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IamportWebhookRequestDto {
    private String impUid;
    private String merchantUid;
    private String status;        // paid, cancelled 등
    private Integer amount;       // 취소된 금액
    private String cancelReason;  // PG 측 취소 사유
}
