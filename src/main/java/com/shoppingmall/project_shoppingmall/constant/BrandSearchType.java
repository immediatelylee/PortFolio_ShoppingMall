package com.shoppingmall.project_shoppingmall.constant;

import lombok.*;

public enum BrandSearchType {
    BRAND_NM("브랜드명"),
    BRAND_CODE("브랜드코드");

    @Getter
    private String description;

    BrandSearchType(String description) {

        this.description = description;
    }


}
