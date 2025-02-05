package com.shoppingmall.project_shoppingmall.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class OptionValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String value; // 예: "블랙", "화이트", "S", "M" 등

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private Option option;

    // 필요하다면 여기서 개별 재고, 추가금, 이미지 등도 확장 가능
}
