package com.shoppingmall.project_shoppingmall.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class WishlistRequest {
    // 상품리스트에서 상품에 있는 위시리스트 클릭시 사용
    @NotNull(message = "상품 아이디는 필수 입력 값 입니다.")
    private Long itemId;
}
