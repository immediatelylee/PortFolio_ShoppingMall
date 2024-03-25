package com.shoppingmall.project_shoppingmall.dto;

import com.shoppingmall.project_shoppingmall.domain.*;
import lombok.*;
import org.modelmapper.*;

@Getter @Setter
public class BrandSimpleDto {
    private Long id;

    private String brandNm;

    public BrandSimpleDto(Long id, String brandNm) {
        this.id = id;
        this.brandNm = brandNm;
    }

}
