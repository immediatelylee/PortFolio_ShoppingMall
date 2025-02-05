package com.shoppingmall.project_shoppingmall.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//241225추가

@Entity
@Getter
@Setter
public class OptionSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Option 목록을 순서를 유지하는 List로 관리
    @OneToMany(mappedBy = "optionSet", cascade = CascadeType.ALL)
    @OrderColumn(name = "option_index")
    private List<Option> options = new ArrayList<>();
}
