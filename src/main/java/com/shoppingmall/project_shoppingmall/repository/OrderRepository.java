package com.shoppingmall.project_shoppingmall.repository;

import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.domain.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o " +
            "where o.member.email = :email " +
            "order by o.orderDate desc"
    )
    List<Order> findOrders(@Param("email") String email, Pageable pageable);

    @Query("select count(o) from Order o " +
            "where o.member.email = :email"
    )
    Long countOrder(@Param("email") String email);
    Order findByMemberAndOrderStatus(Member member, OrderStatus orderStatus);
}
