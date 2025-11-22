package com.shoppingmall.project_shoppingmall.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.*;

@Getter
@Setter
public class OrderRequestDto {

    @NotEmpty(message = "주문할 상품이 없습니다.")
    private List<CartItemOrderDto> items;
}
