package com.shoppingmall.project_shoppingmall.repository;

import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import org.springframework.data.domain.*;

import java.util.*;

public interface ItemRepositoryCustom {



// 메인
    Page<ItemFormDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Long countAllItems();
    Long countItemsBySellStatus(ItemSellStatus sellStatus);
    Long countItemsByDisplayStatus(ItemDisplayStatus displayStatus);

    List<Item> findItemsBySellStatus(ItemSellStatus sellStatus);
    List<Item> findItemsByDisplayStatus(ItemDisplayStatus displayStatus);

    List<ItemWithImgDto> findItemsWithImgsBySubCategory(String subCategory);

}

