package com.shoppingmall.project_shoppingmall.dto;

import com.querydsl.core.annotations.*;
import com.shoppingmall.project_shoppingmall.constant.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MainItemDto {

    private Long id;
    private String itemCode;
    private String itemNm;

    private ItemSellStatus itemSellStatus;

    private ItemDisplayStatus itemDisplayStatus;
    private String imgUrl;

    private Integer price;

    @QueryProjection
    public MainItemDto(Long id,String itemCode, String itemNm,ItemSellStatus itemSellStatus,ItemDisplayStatus itemDisplayStatus , String imgUrl,Integer price){
        this.id = id;
        this.itemCode = itemCode;
        this.itemNm = itemNm;
        this.itemDisplayStatus = itemDisplayStatus;
        this.itemSellStatus = itemSellStatus;
        this.imgUrl = imgUrl;
        this.price = price;
    }

}
