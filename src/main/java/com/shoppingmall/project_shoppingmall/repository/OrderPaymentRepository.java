package com.shoppingmall.project_shoppingmall.repository;

import com.shoppingmall.project_shoppingmall.domain.*;
import org.springframework.data.jpa.repository.*;

import java.util.Optional;

public interface OrderPaymentRepository extends JpaRepository<OrderPayment,Long> {
    Optional<OrderPayment> findByOrder_OrderUid(String orderUid);
}
