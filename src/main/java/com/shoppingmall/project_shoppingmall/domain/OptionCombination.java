package com.shoppingmall.project_shoppingmall.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class OptionCombination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String combination; // 예: "블랙-90"

    private int stock; // 재고

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
}
