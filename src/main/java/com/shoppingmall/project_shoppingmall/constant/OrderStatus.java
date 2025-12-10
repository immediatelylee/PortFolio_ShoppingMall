package com.shoppingmall.project_shoppingmall.constant;

public enum OrderStatus {
    PENDING,    // 주문 생성, 결제 전
    PAID,       // 결제 완료
    CANCELLED,  // 주문 취소
    FAILED      // 결제 실패 등
}

