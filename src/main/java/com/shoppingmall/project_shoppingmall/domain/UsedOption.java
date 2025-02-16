package com.shoppingmall.project_shoppingmall.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
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
