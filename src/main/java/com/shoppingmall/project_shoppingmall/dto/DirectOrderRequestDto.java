package com.shoppingmall.project_shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 상품 상세에서 바로구매 요청
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DirectOrderRequestDto {
    private Long itemId;
    private int count;
}
