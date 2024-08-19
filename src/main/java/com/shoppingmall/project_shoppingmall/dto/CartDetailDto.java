package com.shoppingmall.project_shoppingmall.dto;

import com.shoppingmall.project_shoppingmall.domain.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class CartDetailDto {
// jackson 같은 라이브러리가 객체를 역직렬화 할때 기본생성자를 사용할수 있도록함.

    private Long cartItemId; //장바구니 상품 아이디

    private String itemNm; //상품명

    private String itemCode; //상품코드

    private int price; //상품 금액

    private int count; //수량

    private String imgUrl; //상품 이미지 경로

    // imgUrl 포함한 생성자.
    public CartDetailDto(Long cartItemId, String itemNm,String itemCode, int price, int count, String imgUrl){
        this.cartItemId = cartItemId;
        this.itemNm = itemNm;
        this.itemCode = itemCode;
        this.price = price;
        this.count = count;
        this.imgUrl = imgUrl;
    }

    // imgUrl을 제외한 생성자
    public CartDetailDto(Long cartItemId, String itemNm, String itemCode, int price, int count) {
        this.cartItemId = cartItemId;
        this.itemNm = itemNm;
        this.itemCode = itemCode;
        this.price = price;
        this.count = count;
        this.imgUrl = null;  // imgUrl은 null로 설정
    }
}
