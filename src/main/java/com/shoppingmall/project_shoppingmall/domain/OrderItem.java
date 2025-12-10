package com.shoppingmall.project_shoppingmall.domain;

import lombok.*;

import javax.persistence.*;
import java.math.*;

@Entity
@Getter @Setter
@NoArgsConstructor
public class OrderItem extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    private String productName;

    private int quantity;

    private BigDecimal price;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;


    @Builder
    public OrderItem(String productName, int quantity, BigDecimal price ,String imageUrl,Order order) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl=imageUrl;
        this.order=order;
    }

    public BigDecimal getTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }



}
