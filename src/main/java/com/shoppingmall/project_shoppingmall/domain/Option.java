package com.shoppingmall.project_shoppingmall.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "shop_option")
public class Option { // → 여기서는 "옵션 그룹명" 역할
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 예: "색상", "사이즈"
    private String code; // "O0000001".. "O0000002" prefix 는 영문자 O

    // 기존 Set 대신 List 사용 + @OrderColumn으로 순서를 유지
    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "order_index")  // DB 테이블에 order_index 컬럼이 생성됨
    private List<OptionValue> optionValues = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_set_id")
    private OptionSet optionSet;

//    public void addOptionValue(OptionValue ov) {
//        this.optionValues.add(ov);
//        ov.setOption(this);
//    }
}
