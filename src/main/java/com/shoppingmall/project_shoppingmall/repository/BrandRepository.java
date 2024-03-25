package com.shoppingmall.project_shoppingmall.repository;

import com.shoppingmall.project_shoppingmall.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.*;

import java.util.*;

public interface BrandRepository extends JpaRepository<Brand,Long> ,
        QuerydslPredicateExecutor<Brand>,BrandRepositoryCustom {

    List<Brand> findByBrandNm(String brandNm);

}
