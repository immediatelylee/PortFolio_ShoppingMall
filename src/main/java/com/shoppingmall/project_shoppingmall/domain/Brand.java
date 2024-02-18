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
    private String brandNm;
    private String brandDescription;

    public Brand(Long id, String brandNm, String brandDescription) {
        this.id = id;
        this.brandNm = brandNm;
        this.brandDescription = brandDescription;
    }

    public static BrandSimpleDto fromBrand(Brand brand) {
        if (brand == null) {
            return null;
        }
        return new BrandSimpleDto(
                brand.getId(),
                brand.getBrandNm()
        );
    }
    public static Brand toBrand(BrandSimpleDto brandSimpleDto) {
        if (brandSimpleDto == null) {
            return null;
        }
        return new Brand(
                brandSimpleDto.getId(),
                brandSimpleDto.getBrandNm(),
                null
        );
    }

    public void updateBrand(BrandFormDto brandFormDto){
        this.id = brandFormDto.getId();
        this.brandNm = brandFormDto.getBrandNm();
        this.brandDescription = brandFormDto.getBrandDescription();
    }

}
