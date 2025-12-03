package com.shoppingmall.project_shoppingmall.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderPayRequestDto {
    //requestPay()하기전 initPay()에서 order와 payment를 만들기 위하여
    //서비스의  createOrderWithPayment 에서 사용하는 dto

//    private List<CartDetailDto> cartDetailDtoList; // 장바구니에서 가져온 상품 목록
//    private String paymentMethod; // 결제 방법

    private String orderUid;
    private String paymentMethod;
    private BigDecimal amount;
    private String payKey;  // 있으면
    private String pgTid;   // 있으면
}
