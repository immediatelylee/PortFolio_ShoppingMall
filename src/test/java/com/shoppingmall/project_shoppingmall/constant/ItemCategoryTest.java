package com.shoppingmall.project_shoppingmall.constant;

import com.shoppingmall.project_shoppingmall.domain.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ItemCategoryTest {
    @Test
    @DisplayName("카테고리 도메인 테스트 - DIGITAL의 이름은 가전/디지털 이다.")
    void digitalGetTitleTest() {
        String heelTitle = ItemCategory.DIGITAL.getTitle();

        assertEquals(heelTitle, "가전/디지털");
    }

    @Test
    @DisplayName("카테고리 도메인 테스트 - ROOT의 상위카테고리는 없다")
    void rootGetParentCategoryIsEmptyTest() {
        Optional<ItemCategory> rootParentCategory = ItemCategory.ROOT.getParentItemCategory();

        assertTrue(rootParentCategory.isEmpty());
    }

    @Test
    @DisplayName("카테고리 도메인 테스트 - MEAT_EGG의 상위카테고리는 FOOD이다.")
    void meatEggGetParentCategoryIsFoodTest() {
        Optional<ItemCategory> meatEggParentCategory = ItemCategory.MEAT_EGG.getParentItemCategory();

        assertEquals(meatEggParentCategory.get(), ItemCategory.FOOD);
    }



    @Test
    @DisplayName("카테고리 도메인 테스트 - FASHION_CHILDREN의 하위카테고리에 TV가 없다.")
    void fashionChildrenGetSubCategoriesNotContainsTVTest() {
        List<ItemCategory> fashionChildrenSubCategories = ItemCategory.FASHION_CHILDREN.getChildCategories();

        assertFalse(fashionChildrenSubCategories.contains(ItemCategory.TV));
    }

    @Test
    @DisplayName("카테고리 도메인 테스트 - MEN_PANTS는 리프카테고리다.")
    void menPantsIsLeafCategoryIsTrueTest() {
        assertTrue(ItemCategory.MEN_PANTS.isLeafItemCategory());
    }

    @Test
    @DisplayName("카테고리 도메인 테스트 - 하위카테고리 변경 금지 테스트")
    void subCategoriesLisIsUnmodifiableTest() {
        assertThrows(UnsupportedOperationException.class,
                () -> ItemCategory.MEAT_EGG.getChildCategories().add(ItemCategory.BEVERAGE)
        );
    }

    @Test
    @DisplayName("카테고리 도메인 테스트 - [TV, PROJECTOR, COMPUTER]는 DIGITAL의 리프카테고리다.")
    void TVAndProjectorAndComputerAreLeafCategoryOfDigitalTest() {
        List<ItemCategory> leafCategories = ItemCategory.DIGITAL.getLeafCategories();

        assertTrue(leafCategories.contains(ItemCategory.TV));
        assertTrue(leafCategories.contains(ItemCategory.PROJECTOR));
        assertTrue(leafCategories.contains(ItemCategory.COMPUTER));
    }

    @Test
    @DisplayName("카테고리 도메인 테스트 - INSTANT는 FASHION_WOMEN의 리프카테고리가 아니다.")
    void InstantIsNotLeafCategoryOfFashionWomenTest() {
        List<ItemCategory> leafCategories = ItemCategory.FASHION_WOMEN.getLeafCategories();

        assertFalse(leafCategories.contains(ItemCategory.INSTANT));
    }

    @Test
    void print() {
        System.out.println(ItemCategory.FOOD.getParentItemCategory());
    }

    @Test
    void test1(){

        ItemCategory itemCategory = ItemCategory.titleOf("지갑");

        System.out.println(itemCategory);
    }

}