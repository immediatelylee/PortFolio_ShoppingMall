package com.shoppingmall.project_shoppingmall.repository;

import com.shoppingmall.project_shoppingmall.domain.Item;
import com.shoppingmall.project_shoppingmall.domain.UsedOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsedOptionRepository extends JpaRepository<UsedOption,Long> {
    // 특정 Item에 연관된 UsedOption 조회
    List<UsedOption> findByItem(Item item);
}
