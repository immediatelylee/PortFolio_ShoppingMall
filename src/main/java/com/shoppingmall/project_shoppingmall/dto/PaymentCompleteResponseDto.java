package com.shoppingmall.project_shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompleteResponseDto {
    private boolean success;
    private String redirectUrl;
    private String message;
}
