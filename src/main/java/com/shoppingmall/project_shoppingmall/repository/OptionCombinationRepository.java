package com.shoppingmall.project_shoppingmall.repository;

import com.shoppingmall.project_shoppingmall.domain.Item;
import com.shoppingmall.project_shoppingmall.domain.OptionCombination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionCombinationRepository extends JpaRepository<OptionCombination,Long> {
    List<OptionCombination> findByItem(Item item);

    void deleteByItem(Item item);
}
