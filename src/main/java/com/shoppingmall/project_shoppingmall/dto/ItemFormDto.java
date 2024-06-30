package com.shoppingmall.project_shoppingmall.dto;

import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.domain.*;
import lombok.*;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @ToString
public class ItemFormDto {

    private Long id;

    private String itemCode;

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String itemNm;

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price;

    @NotNull(message = "재고는 필수 입력 값입니다.")
    private Integer stockNumber;

    @NotBlank(message = "상품 상세는 필수 입력 값입니다.")
    private String itemDetail;

    private ItemSellStatus itemSellStatus;

    private ItemDisplayStatus itemDisplayStatus;

//    @NotNull(message = "카테고리는 필수 입력값입니다.")
    private String category;
    private String subcategory1;
    private String subcategory2;

    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();

    private List<ItemDetailImgDto> itemDetailImgDtoList = new ArrayList<>();

    private List<Long> itemImgIds = new ArrayList<>();

    private List<Long> itemDetailImgIds = new ArrayList<>();

    private Long brandId;

    // 썸네일 이미지 URL
    private String thumbnailImgUrl;

    public Item toItem(){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(this, Item.class);
    }

    public static ItemFormDto of(Item item){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(item,ItemFormDto.class);
    }
    public static ItemFormDto addThumbnail(Item item,String thumbnailImgUrl){
        ModelMapper modelMapper = new ModelMapper();
        ItemFormDto itemFormDto = modelMapper.map(item,ItemFormDto.class);

        itemFormDto.setThumbnailImgUrl(thumbnailImgUrl);
        return itemFormDto;

    }

}
