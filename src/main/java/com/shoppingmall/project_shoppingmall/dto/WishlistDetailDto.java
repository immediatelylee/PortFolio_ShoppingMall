package com.shoppingmall.project_shoppingmall.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WishlistDetailDto {
    // jackson 같은 라이브러리가 객체를 역직렬화할 때 기본 생성자를 사용할 수 있도록 함.

    private Long wishlistItemId; // 위시리스트 상품 아이디

    private Long itemId; // 상품 아이디

    private String itemNm; // 상품명

    private String itemCode; // 상품 코드

    private int price; // 상품 금액

    private String imgUrl; // 상품 이미지 경로

    // imgUrl 포함한 생성자
    public WishlistDetailDto(Long wishlistItemId, Long itemId, String itemNm, String itemCode, int price, String imgUrl) {
        this.wishlistItemId = wishlistItemId;
        this.itemId = itemId;
        this.itemNm = itemNm;
        this.itemCode = itemCode;
        this.price = price;
        this.imgUrl = imgUrl;
    }

    // imgUrl을 제외한 생성자
    public WishlistDetailDto(Long wishlistItemId, Long itemId, String itemNm, String itemCode, int price) {
        this.wishlistItemId = wishlistItemId;
        this.itemId = itemId;
        this.itemNm = itemNm;
        this.itemCode = itemCode;
        this.price = price;
        this.imgUrl = null; // imgUrl은 null로 설정
    }

}
