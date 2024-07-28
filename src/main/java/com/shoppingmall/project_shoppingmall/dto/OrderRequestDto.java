package com.shoppingmall.project_shoppingmall.dto;

import lombok.*;

import java.util.*;

@Getter
@Setter
public class OrderRequestDto {
    private List<CartItemOrderDto> items;
}
