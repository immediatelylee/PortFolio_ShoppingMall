package com.shoppingmall.project_shoppingmall.repository;

import com.shoppingmall.project_shoppingmall.domain.*;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface BrandImgRepository extends JpaRepository<BrandImg, Long> {
    List<BrandImg> findByBrandIdOrderByIdAsc(Long brandId);

}
