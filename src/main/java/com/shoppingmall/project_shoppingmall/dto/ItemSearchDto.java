package com.shoppingmall.project_shoppingmall.dto;

import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.domain.*;
import lombok.*;
import org.springframework.util.StringUtils;

@Getter
@Setter
public class ItemSearchDto {

    private String searchDateType;

    private ItemSellStatus searchSellStatus;

    private ItemDisplayStatus searchDisplayStatus;

    private ItemSearchType searchBy;

    private String searchQuery = "";

    private String mainCategory;
    private String subCategory;
    private String subSubCategory;

    // 검색 조건이 설정되어 있는지 확인하는 메소드
    public boolean hasSearchConditions() {
        return searchBy != null || StringUtils.hasText(searchQuery)
                || searchSellStatus != null || searchDisplayStatus != null
                || StringUtils.hasText(searchDateType)
                || StringUtils.hasText(mainCategory)
                || StringUtils.hasText(subCategory)
                || StringUtils.hasText(subSubCategory);
    }
}

