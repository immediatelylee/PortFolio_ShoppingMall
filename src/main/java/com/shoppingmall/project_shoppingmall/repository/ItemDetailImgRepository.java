package com.shoppingmall.project_shoppingmall.repository;

import com.shoppingmall.project_shoppingmall.domain.*;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface ItemDetailImgRepository extends JpaRepository<ItemDetailImg, Long> {
    List<ItemDetailImg> findByItemIdOrderByIdAsc(Long itemId);
    List<ItemDetailImg> findByItemId(Long itemId);
}
