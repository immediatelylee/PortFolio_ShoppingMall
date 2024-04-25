package com.shoppingmall.project_shoppingmall.domain;

import com.shoppingmall.project_shoppingmall.dto.*;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "brand")
@Getter
@Setter
@ToString
public class Brand extends BaseEntity{

    @Id
    @Column(name="brand_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String brandNm;

    @Column(name = "brand_status")
    private boolean brandStatus;

    @Column(name = "brand_code")
    private String brandCode;

    public void updateBrand(BrandFormDto brandFormDto){
        this.id = brandFormDto.getId();
        this.brandNm = brandFormDto.getBrandNm();
        this.brandStatus = brandFormDto.isBrandStatus();
        this.brandCode = brandFormDto.getBrandCode();
    }

}
