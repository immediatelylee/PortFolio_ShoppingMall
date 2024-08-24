package com.shoppingmall.project_shoppingmall.dto;

import com.querydsl.core.annotations.*;
import lombok.*;

@Getter
@Setter
public class ItemWithImgDto {

    private Long itemId;
    private String itemNm;
    private int price;
    private String itemDetail;
    private String itemSellStatus;
    private String itemDisplayStatus;
    private String mainCategory;
    private String subCategory;
    private String subSubCategory;
    private String brandNm;
    private String itemImgUrl;

    @QueryProjection
    public ItemWithImgDto(Long itemId, String itemNm, int price, String itemDetail, String itemSellStatus,
                          String itemDisplayStatus, String mainCategory, String subCategory,
                          String subSubCategory, String brandNm, String itemImgUrl) {
        this.itemId = itemId;
        this.itemNm = itemNm;
        this.price = price;
        this.itemDetail = itemDetail;
        this.itemSellStatus = itemSellStatus;
        this.itemDisplayStatus = itemDisplayStatus;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.subSubCategory = subSubCategory;
        this.brandNm = brandNm;
        this.itemImgUrl = itemImgUrl;
    }
}
