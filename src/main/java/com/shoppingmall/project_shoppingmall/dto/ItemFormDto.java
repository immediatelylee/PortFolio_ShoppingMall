package com.shoppingmall.project_shoppingmall.dto;

import com.querydsl.core.annotations.*;
import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.domain.*;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.springframework.ui.*;

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

    //상품 등록페이지 기본값 설정을 위한 초기값 설정(thymeleaf 기능때문)
    private ItemSellStatus itemSellStatus = ItemSellStatus.SOLD_OUT;

    //상품 등록페이지 기본값 설정을 위한 초기값 설정(thymeleaf 기능때문)
    private ItemDisplayStatus itemDisplayStatus = ItemDisplayStatus.NOT_DISPLAY;


//    @NotNull(message = "카테고리는 필수 입력값입니다.")
    private String mainCategory;
    private String subCategory;
    private String subSubCategory;
// 상품 수정시 이미지 정보 로드
    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();

    private List<ItemDetailImgDto> itemDetailImgDtoList = new ArrayList<>();

    private List<ItemThumbnailDto> itemThumbnailDtoList = new ArrayList<>();
//
    private String color;
    private Integer size;

    private Long brandId;

    // 썸네일 이미지 URL
    private String thumbnailImgUrl;

    // [옵션 관련]
    private OptionDisplayType displayType;
    // COMBINED(조합 일체형), SEPARATED(분리 선택형), NONE(옵션 미사용) 등

//    // 조합 일체형에서 쓸 옵션 조합 리스트
//    // 예: ["블랙/S", "블랙/M", "화이트/S"]
//    private List<String> combinationList = new ArrayList<>();


    // 조합 일체형에서 쓸 옵션 조합 리스트
    // 예: ["블랙/S", "블랙/M", "화이트/S"]
    private List<String> combinationList;
//    private List<String> mp_item_image;
    private List<String> stockOption_use;   // "T" 또는 "F" 값 (재고 사용 여부)
//    private List<String> mp_stock_type;
//    private List<String> mp_sold_out_stock_type;
    private List<Integer> itemOption_stock;        // 옵션별 재고 수량
//    private List<Integer> mp_stock_warn_value;

    public Item toItem(){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(this, Item.class);
    }

    public static ItemFormDto of(Item item){
        ModelMapper modelMapper = new ModelMapper();
        ItemFormDto dto = modelMapper.map(item, ItemFormDto.class);

        // (추가) OptionCombination -> combinationList 변환
        if (item.getOptionCombinations() != null && !item.getOptionCombinations().isEmpty()) {
            List<String> combos = new ArrayList<>();
            item.getOptionCombinations().forEach(oc -> {
                combos.add(oc.getCombination());
            });
            dto.setCombinationList(combos);
        }
        // brand -> brandId는 Service 단에서나,
        // or else if (item.getBrand()!=null) dto.setBrandId(item.getBrand().getId());
        // ...
        return dto;
    }
    public static ItemFormDto addThumbnail(Item item,String thumbnailImgUrl){
        ModelMapper modelMapper = new ModelMapper();
        ItemFormDto itemFormDto = modelMapper.map(item,ItemFormDto.class);

        itemFormDto.setThumbnailImgUrl(thumbnailImgUrl);
        return itemFormDto;

    }

    // 기본 생성자 추가
    public ItemFormDto() {}

    @QueryProjection
    public ItemFormDto(Long id,String itemCode,String itemNm, ItemDisplayStatus itemDisplayStatus, ItemSellStatus itemSellStatus, String thumbnailImgUrl, Integer price
                        ,String mainCategory,String subCategory,String subSubCategory) {
        this.id = id;
        this.itemCode =itemCode;
        this.itemNm = itemNm;
        this.itemDisplayStatus = itemDisplayStatus;
        this.itemSellStatus = itemSellStatus;
        this.thumbnailImgUrl = thumbnailImgUrl;
        this.price = price;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.subSubCategory = subSubCategory;
    }
}
