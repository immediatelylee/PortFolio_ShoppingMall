package com.shoppingmall.project_shoppingmall.domain;

import com.shoppingmall.project_shoppingmall.constant.*;
import lombok.*;

import javax.persistence.*;
import java.math.*;
import java.time.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor
public class Order extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

//    orderItem에 추가
//    private Long price;

    private String orderName;

    @Column(unique = true, nullable = false)
    private String orderUid; // 주문 번호 (PG 연동용)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate; //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; //주문상태

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL
            , orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private OrderPayment orderPayment;

    // 주문 생성자 (PENDING 상태)
    @Builder
    private Order(Member member, String orderUid, LocalDateTime orderDate, OrderStatus orderStatus) {
        this.member = member;
        this.orderUid = orderUid;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
    }

//    public Order(LocalDateTime orderDate) {
//        this.orderDate = orderDate;
//    }
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
        updateOrderName(); // orderName 갱신
    }

    // 안쓰일수도 있음 삭제 예정
    public void updateOrderName() {
        if (orderItems.isEmpty()) {
            this.orderName = "No items";
            return;
        }

        String firstProductName = orderItems.get(0).getProductName();
        int remainingItemsCount = orderItems.size() - 1;

        if (remainingItemsCount > 0) {
            this.orderName = firstProductName + " 외 " + remainingItemsCount + "건";
        } else {
            this.orderName = firstProductName;
        }
    }

    public BigDecimal getTotalPrice() {
        return orderItems.stream()
                .map(OrderItem::getTotalPrice)  // 각 OrderItem의 총 금액을 BigDecimal로 가져옴
                .reduce(BigDecimal.ZERO, BigDecimal::add);  // BigDecimal의 합계를 구함
    }


    public void markPaid() {
        this.orderStatus = OrderStatus.PAID;
    }

    public void markFailed() {
        this.orderStatus = OrderStatus.FAILED;
    }

    public void markCancelled() {
        this.orderStatus = OrderStatus.CANCELLED;
    }
}
