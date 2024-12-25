package com.shoppingmall.project_shoppingmall.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//241225추가

@Entity
@Getter
@Setter
public class OptionSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "optionSet", cascade = CascadeType.ALL)
    private List<Option> options = new ArrayList<>();
}
