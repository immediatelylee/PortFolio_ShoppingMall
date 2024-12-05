package com.shoppingmall.project_shoppingmall.repository;

import com.shoppingmall.project_shoppingmall.domain.CartItem;
import com.shoppingmall.project_shoppingmall.domain.WishlistItem;
import com.shoppingmall.project_shoppingmall.dto.WishlistDetailDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem,Long> {
    WishlistItem findByWishlistIdAndItemId(Long wishlistId, Long itemId);

    // 사용자의 모든 위시리스트 를 조회하기 위함.

    @Query("select new com.shoppingmall.project_shoppingmall.dto.WishlistDetailDto(wi.id, i.id, i.itemNm, i.itemCode, i.price, im.imgUrl) " +
            "from WishlistItem wi, ItemImg im " +
            "join wi.item i " +
            "where wi.wishlist.id = :wishlistId " +
            "and im.item.id = wi.item.id " +
            "and im.repimgYn = 'Y' " +
            "order by wi.regTime desc")
    List<WishlistDetailDto> findWishlistDetailDtoList(@Param("wishlistId") Long wishlistId);


    // 사용자의 위시리스트를 분석하여 해당 상품들의 id들을 model.add하기 위함. 이후 상품리스트에서 위시리스트에 있으면 하트 표시

    @Query("SELECT wi.item.id " +
            "FROM WishlistItem wi " +
            "JOIN wi.wishlist w " +
            "WHERE w.member.email = :email")
    List<Long> findItemIdsByMemberEmail(@Param("email") String email);

    // itemId로 WishlistItem 찾기- wishlistItem단일 삭제 optional로 지정
    Optional<WishlistItem> findByItemId(Long itemId);
}
