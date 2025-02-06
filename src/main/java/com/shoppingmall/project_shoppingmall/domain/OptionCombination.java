package com.shoppingmall.project_shoppingmall.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


/**
 * “블랙/S” 처럼, 여러 OptionValue가 결합된 최종 조합
 * - stock: 해당 조합의 재고
 * - item: 어느 상품에 속하는지
 */
@Entity
@Getter
@Setter
public class OptionCombination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String combination; // 예: "블랙/90"

    private int stock; // 재고
    private boolean useManageStock;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
}
