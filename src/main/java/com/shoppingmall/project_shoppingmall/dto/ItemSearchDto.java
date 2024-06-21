package com.shoppingmall.project_shoppingmall.dto;

import com.shoppingmall.project_shoppingmall.constant.*;
import lombok.*;

@Getter
@Setter
public class ItemSearchDto {

    private String searchDateType;

    private ItemSellStatus searchSellStatus;

    private ItemDisplayStatus searchDisplayStatus;

    private String searchBy;

    private String searchQuery = "";

}

