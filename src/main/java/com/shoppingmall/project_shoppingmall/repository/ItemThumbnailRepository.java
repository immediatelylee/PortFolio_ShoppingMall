package com.shoppingmall.project_shoppingmall.repository;

import com.shoppingmall.project_shoppingmall.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.*;
import org.springframework.data.repository.query.*;

import java.util.*;

public interface ItemThumbnailRepository extends JpaRepository<ItemThumbnail, Long>, QuerydslPredicateExecutor<Item>,ItemRepositoryCustom {
//  쿼리문 테스트
    @Query("SELECT it.imgUrl FROM ItemThumbnail it WHERE it.item.id = :itemId")
    String findThumbnailUrlByItemId(@Param("itemId") Long itemId);

    List<ItemThumbnail> findByItemIdOrderByIdAsc(Long itemId);

//    String findImgurlByItemId(Long itemId);


}

