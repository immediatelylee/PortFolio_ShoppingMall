package com.shoppingmall.project_shoppingmall.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequestDto {
    // ===========================================
    //  상품 -> 주문 (direct)시 itemDetail.html에서 ajax로 보낼 데이터 dto
    // ===========================================
    private Long itemId;
    private int count;
}
