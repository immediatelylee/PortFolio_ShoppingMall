package com.shoppingmall.project_shoppingmall.repository;

import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    @Query("select new com.shoppingmall.project_shoppingmall.dto.CartDetailDto(ci.id, i.itemNm,i.itemCode ,i.price, ci.count, im.imgUrl) " +
            "from CartItem ci, ItemImg im " +
            "join ci.item i " +
            "where ci.cart.id = :cartId " +
            "and im.item.id = ci.item.id " +
            "and im.repimgYn = 'Y' " +
            "order by ci.regTime desc"
    )
    List<CartDetailDto> findCartDetailDtoList(@Param("cartId")Long cartId);


    // itemthumbnail적용
    @Query("select new com.shoppingmall.project_shoppingmall.dto.CartDetailDto(ci.id, i.itemNm, i.itemCode, i.price, ci.count, it.imgUrl) " +
            "from CartItem ci " +
            "join ci.item i " +
            "left join ItemThumbnail it on it.item.id = i.id " +
            "where ci.cart.id = :cartId " +
            "and ci.id in :itemIds " +
            "order by ci.regTime desc")
    List<CartDetailDto> findCartDetailDtoListByItemIds(@Param("cartId") Long cartId, @Param("itemIds") List<Long> itemIds);

}
