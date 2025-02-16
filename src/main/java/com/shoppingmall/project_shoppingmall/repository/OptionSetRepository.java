package com.shoppingmall.project_shoppingmall.repository;

import com.shoppingmall.project_shoppingmall.domain.OptionSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OptionSetRepository extends JpaRepository<OptionSet,Long> {

    @Query("SELECT DISTINCT s FROM OptionSet s " +
            "LEFT JOIN FETCH s.options o " +
            "LEFT JOIN FETCH o.optionValues " +
            "WHERE s.id = :id")
    OptionSet findByIdFetchAll(@Param("id") Long id);


}
