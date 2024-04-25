package com.shoppingmall.project_shoppingmall.repository;

import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.*;

import java.util.*;

public interface BrandRepository extends JpaRepository<Brand,Long> ,
        QuerydslPredicateExecutor<Brand>,BrandRepositoryCustom {

        Page<Brand> findByBrandNmContaining(String title, Pageable pageable);
        Page<Brand> findByBrandCodeContaining(String content, Pageable pageable);

        Page<Brand> findByBrandNmContainingAndBrandStatusIsTrue(String title, Pageable pageable);
        Page<Brand> findByBrandNmContainingAndBrandStatusIsFalse(String title, Pageable pageable);

        Page<Brand> findByBrandCodeContainingAndBrandStatusIsTrue(String content, Pageable pageable);
        Page<Brand> findByBrandCodeContainingAndBrandStatusIsFalse(String content, Pageable pageable);

}
