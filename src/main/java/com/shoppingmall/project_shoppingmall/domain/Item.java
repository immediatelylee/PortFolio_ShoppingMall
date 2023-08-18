package com.shoppingmall.project_shoppingmall.domain;

import com.shoppingmall.project_shoppingmall.domain.constant.*;
import lombok.*;

import javax.persistence.*;
import java.time.*;

@Entity
@Table(name="item")
@Getter
@ToString
public class Item {
    @Id
    @Column(name="item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;       //상품 코드
    @Setter @Column(nullable = false, length = 50)
    private String itemNm; //상품명

    @Setter @Column(name="price", nullable = false)
    private int price; //가격

    @Setter @Column(nullable = false)
    private int stockNumber; //재고수량

    @Setter @Lob @Column(nullable = false)
    private String itemDetail; //상품 상세 설명

    @Setter @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; //상품 판매 상태

    @Setter
    private LocalDateTime regTime; //등록 시간

    @Setter
    private LocalDateTime updateTime; //수정 시간
}
