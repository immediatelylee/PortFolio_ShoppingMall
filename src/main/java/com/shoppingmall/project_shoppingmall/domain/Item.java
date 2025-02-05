package com.shoppingmall.project_shoppingmall.domain;

import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import com.shoppingmall.project_shoppingmall.exception.*;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity {

    @Id
    @Column(name="item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;       //상품 id

    @Column(name = "item_code")
    private String itemCode;

    @Column(nullable = false, length = 50)
    private String itemNm; //상품명

    @Column(name="price", nullable = false)
    private int price; //가격

    @Column(nullable = false)
    private int stockNumber; //재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail; //상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; //상품 판매 상태

    @Enumerated(EnumType.STRING)
    private ItemDisplayStatus itemDisplayStatus; // 상품 진열 상태

    private String mainCategory;
    private String subCategory;
    private String subSubCategory;

    private String color;
    private Integer size;

    //    추가
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    // [1] 옵션 표시방식을 구분하는 필드
    @Enumerated(EnumType.STRING)
    private OptionDisplayType displayType;
    // 예) COMBINED(조합 일체형) / SEPARATED(분리 선택형) / 혹은 null이면 옵션 미사용 등

    // [2] 조합 일체형 옵션: Item : OptionCombination = 1 : N
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptionCombination> optionCombinations = new ArrayList<>();

    //편의 메서드
    public void addOptionCombination(OptionCombination oc) {
        this.optionCombinations.add(oc);
        oc.setItem(this);
    }

    public void updateItem(ItemFormDto itemFormDto,Brand brand){
        this.itemNm = itemFormDto.getItemNm();
        this.itemCode = itemFormDto.getItemCode();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
        this.itemDisplayStatus = itemFormDto.getItemDisplayStatus();
        this.mainCategory = itemFormDto.getMainCategory();
        this.subCategory = itemFormDto.getSubCategory();
        this.subSubCategory = itemFormDto.getSubSubCategory();
        this.brand=brand;
        this.color = itemFormDto.getColor();
        this.size = itemFormDto.getSize();
    }

    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber;
        if(restStock<0){
            throw new OutOfStockException("상품의 재고가 부족 합니다. (현재 재고 수량: " + this.stockNumber + ")");
        }
        this.stockNumber = restStock;
    }
    // 주문 취소시 재고 추가
    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }

}
