package com.shoppingmall.project_shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelResponseDto {

    private boolean success;
    private String message;
    private String redirectUrl;   // 프론트에서 이동할 주소 (예: "/cart")
}
