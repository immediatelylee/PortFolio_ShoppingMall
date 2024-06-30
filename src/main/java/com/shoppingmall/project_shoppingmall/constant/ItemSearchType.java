package com.shoppingmall.project_shoppingmall.constant;

import lombok.*;

public enum ItemSearchType {
    ITEM_NM("상품명"),
    ITEM_CODE("상품코드"),
    BRAND_NM("브랜드명"),
    BRAND_CODE("브랜드코드");

    @Getter
    private String description;

    ItemSearchType(String description) {

        this.description = description;
    }


}
