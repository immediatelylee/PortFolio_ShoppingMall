package com.shoppingmall.project_shoppingmall.constant;

import java.util.*;
import java.util.stream.*;

public enum ItemCategory {
    ROOT("카테고리", null,0L),
        FASHION("패션의류/잡화", ROOT,1L),
            FASHION_MEN("남성", FASHION,2L),
                MEN_T_SHIRT("티셔츠", FASHION_MEN,3L),
                MEN_SWEATSHIRT_HOOD("스웻셔츠/후드", FASHION_MEN,3L),
                MEN_SHIRT("셔츠", FASHION_MEN,3L),
                MEN_SUIT("정장", FASHION_MEN,3L),
                MEN_PANTS("바지", FASHION_MEN,3L),
            FASHION_WOMEN("여성", FASHION,2L),
                WOMEN_T_SHIRT("티셔츠", FASHION_WOMEN,3L),
                WOMEN_BLOUSE("블라우스/셔츠", FASHION_WOMEN,3L),
                WOMEN_SWEATSHIRT_HOOD("스웻셔츠/후드", FASHION_WOMEN,3L),
                WOMEN_SUIT("정장", FASHION_WOMEN,3L),
                WOMEN_ONE_PIECE("원피스", FASHION_WOMEN,3L),
                WOMEN_SKIRT("치마", FASHION_WOMEN,3L),
                WOMEN_PANTS("바지", FASHION_WOMEN,3L),
            UNISEX("남녀공용", FASHION,2L),
                UNISEX_T_SHIRT("티셔츠", UNISEX,3L),
                UNISEX_PANTS("바지", UNISEX,3L),
            BAG_ACC("가방/잡화", FASHION,2L),
                    BACKPACK("백팩", BAG_ACC,3L),
                    CROSS_BAG("크로스백", BAG_ACC,3L),
                    SHOULDER_BAG("숄더백", BAG_ACC,3L),
                    MINI_BAG("미니백", BAG_ACC,3L),
                    ECO_BAG("캔버스/에코백", BAG_ACC,3L),
                    WALLET("지갑", BAG_ACC,3L),
                    BELT("벨트", BAG_ACC,3L),
            SHOES("신발", FASHION,2L),
                SNEAKERS("운동화/스니커즈", SHOES,3L),
                FLAT_SHOES("단화/플랫", SHOES,3L),
                HEEL("힐", SHOES,3L),
                BOOTS("워커/부츠", SHOES,3L),
                SLIPPER("슬리퍼", SHOES,3L),
            FASHION_CHILDREN("아동", FASHION,2L),
                GIRL_FASHION("여아", FASHION_CHILDREN,3L),
                BOY_FASHION("남아", FASHION_CHILDREN,3L),
        FOOD("식품", ROOT,1L),
            INSTANT("가공/즉석식품", FOOD,2L),
            BEVERAGE("생수/음료", FOOD,2L),
            FRESH("신선식품", FOOD,2L),
            MEAT_EGG("축산/계란", FOOD,2L),
            RICE("쌀/잡곡", FOOD,2L),
        DIGITAL("가전/디지털", ROOT,1L),
            VIDEO("TV/영상가전", DIGITAL,2L),
                TV("TV", VIDEO,3L),
                PROJECTOR("프로젝터/스크린", VIDEO,3L),
            COMPUTER("컴퓨터/게임/SW", DIGITAL,2L);

    // 카테고리 이름
    private final String title;

    // 부모 카테고리
    private final ItemCategory parentItemCategory;

    // 자식카테고리
    private final List<ItemCategory> childCategories;

    // depth
    private final Long depth;

    ItemCategory(String title, ItemCategory parentItemCategory,Long depth) {
        this.childCategories = new ArrayList<>();
        this.title = title;
        this.parentItemCategory = parentItemCategory;
        if(Objects.nonNull(parentItemCategory)) {
            parentItemCategory.childCategories.add(this);
        }
        this.depth = depth;
    }

    public String getTitle() {
        return title;
    }

    // 부모카테고리 Getter
    public Optional<ItemCategory> getParentItemCategory() {
        return Optional.ofNullable(parentItemCategory);
    }

    // 자식카테고리 Getter
    public List<ItemCategory> getChildCategories() {
        return Collections.unmodifiableList(childCategories);
    }

    // 마지막 카테고리(상품추가 가능)인지 반환
    public boolean isLeafItemCategory() {
        return childCategories.isEmpty();
    }

    // 마지막 카테고리(상품추가 가능)들 반환
    public List<ItemCategory> getLeafCategories() {
        return Arrays.stream(ItemCategory.values())
                .filter(ItemCategory -> ItemCategory.isLeafItemCategoryOf(this))
                .collect(Collectors.toList());
    }

    private boolean isLeafItemCategoryOf(ItemCategory ItemCategory) {
        return this.isLeafItemCategory() && ItemCategory.contains(this);
    }

    private boolean contains(ItemCategory ItemCategory) {
        if(this.equals(ItemCategory)) return true;

        return Objects.nonNull(ItemCategory.parentItemCategory) &&
                this.contains(ItemCategory.parentItemCategory);
    }

    public Long getDepth() {
        return depth;
    }


    public static ItemCategory titleOf(String title) {
        for (ItemCategory Ltitle : ItemCategory.values()) {
            if (Ltitle.getTitle().equals(title)){
                return Ltitle;
            }
        }
        return null;
    }
}
