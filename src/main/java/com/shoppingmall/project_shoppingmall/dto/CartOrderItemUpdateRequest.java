package com.shoppingmall.project_shoppingmall.dto;

import lombok.*;

@Getter
@Setter
public class CartOrderItemUpdateRequest {
    private Long cartItemId;
    private Long count;
}
