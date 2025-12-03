package com.shoppingmall.project_shoppingmall.repository;

import com.shoppingmall.project_shoppingmall.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {

    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);

    Optional<ItemImg> findByItemIdAndRepimgYn(Long itemId, String repimgYn);

    ItemImg findFirstByItemIdOrderByIdAsc(Long itemId);

    List<ItemImg> findByItemId(Long itemId);
}

