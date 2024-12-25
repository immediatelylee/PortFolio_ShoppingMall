package com.shoppingmall.project_shoppingmall.domain;

import javax.persistence.*;

@Entity
public class UsedOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String optionName;

    private String optionValue;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
}
