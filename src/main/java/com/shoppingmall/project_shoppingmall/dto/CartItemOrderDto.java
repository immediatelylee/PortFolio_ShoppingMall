package com.shoppingmall.project_shoppingmall.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CartItemOrderDto {

    @NotNull(message = "장바구니 상품 아이디는 필수입니다.")
    private Long cartItemId;

    @Min(value = 1, message = "최소 1개 이상 주문해야 합니다.")
    private int count;
}
