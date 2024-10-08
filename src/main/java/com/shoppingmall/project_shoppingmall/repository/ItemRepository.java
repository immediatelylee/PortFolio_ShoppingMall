package com.shoppingmall.project_shoppingmall.repository;

import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.domain.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.*;
import org.springframework.data.repository.query.*;

import java.util.*;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item>,ItemRepositoryCustom {

    List<Item> findByItemNm(String itemNm);

    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);

    List<Item> findByPriceLessThan(Integer price);

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    @Query("select i from Item i where i.itemDetail like " +
            "%:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    @Query(value="select * from item i where i.item_detail like " +
            "%:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);

    List<Item> findByBrand_BrandNm(String brandNm);

    List<Item> findByIdIn(List<Long> itemIds);

    // 판매 상태가 SELL인 모든 항목 조회
    // 판매 상태가 SOLD_OUT인 모든 항목 조회
    Page<Item> findByitemSellStatus(ItemSellStatus sellStatus,Pageable pageable);
//    List<Item> findBySellStatus(ItemSellStatus sellStatus);
    // 진열 상태가 DISPLAY인 모든 항목 조회
    // 진열 상태가 NOT_DISPLAY인 모든 항목 조회
    Page<Item> findByitemDisplayStatus(ItemDisplayStatus displayStatus,Pageable pageable);
//    List<Item> findByDisplayStatus(ItemDisplayStatus displayStatus);

    Long countByBrandId(Long brandId);

}

