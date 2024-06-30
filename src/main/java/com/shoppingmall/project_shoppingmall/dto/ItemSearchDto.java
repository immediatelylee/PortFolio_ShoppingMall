package com.shoppingmall.project_shoppingmall.dto;

import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.domain.*;
import lombok.*;

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

}

