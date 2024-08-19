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
    private String orderUid; // 주문 번호

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

    public Order(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
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

    //    orderItem사용처 주석
//    public static Order createOrder(Member member, List<OrderItem> orderItemList) {
//
//        Order order = new Order();
//        order.setMember(member);
//
//        for(OrderItem orderItem : orderItemList) {
//            order.addOrderItem(orderItem);
//        }
//
//        order.setOrderStatus(OrderStatus.ORDER);
//        order.setOrderDate(LocalDateTime.now());
//        return order;
//    }
//    orderItem사용처 주석
//    public int getTotalPrice() {
//        int totalPrice = 0;
//        for(OrderItem orderItem : orderItems){
//            totalPrice += orderItem.getTotalPrice();
//        }
//        return totalPrice;
//    }
////    orderItem사용처 주석
//    // 주문 취소 로직
//    public void cancelOrder() {
//        this.orderStatus = OrderStatus.CANCEL;
//        for (OrderItem orderItem : orderItems) {
//            orderItem.cancel();
//        }
//    }

}
