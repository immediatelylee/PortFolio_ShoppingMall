package com.shoppingmall.project_shoppingmall.service;

import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import com.shoppingmall.project_shoppingmall.repository.*;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.persistence.*;
import javax.servlet.http.*;
import java.math.*;
import java.security.*;
import java.time.*;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderPaymentRepository orderPaymentRepository;

    @Transactional
    public Order createOrderWithPayment(List<CartDetailDto> cartDetailDtoList,String paymentMethod ) {
        // 1. Order 생성
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());

        // UUID 생성 및 orderUid에 설정
        String orderUid = "order_" + UUID.randomUUID().toString();
        order.setOrderUid(orderUid);

        // 2. OrderItem 생성 및 Order에 추가
        String firstProductName = null;
        int remainingItemsCount = 0;

        for (int i = 0; i < cartDetailDtoList.size(); i++) {
            CartDetailDto cartDetailDto = cartDetailDtoList.get(i);
            OrderItem orderItem = new OrderItem();
            orderItem.setProductName(cartDetailDto.getItemNm());
            orderItem.setPrice(BigDecimal.valueOf(cartDetailDto.getPrice())); // int를 BigDecimal로 변환
            orderItem.setQuantity(cartDetailDto.getCount());
            orderItem.setOrder(order); // 연관 관계 설정

            order.addOrderItem(orderItem); // Order에 OrderItem 추가
            orderItemRepository.save(orderItem); // OrderItem 저장

            // OrderName 설정을 위한 첫 번째 상품 이름과 나머지 항목 수 계산
            if (i == 0) {
                firstProductName = cartDetailDto.getItemNm();
            } else {
                remainingItemsCount++;
            }
        }

        // OrderName 설정
        if (remainingItemsCount > 0) {
            order.setOrderName(firstProductName + " 외 " + remainingItemsCount + "건");
        } else {
            order.setOrderName(firstProductName);
        }

        // 3. Order 저장
        orderRepository.save(order);

        // 4. Payment 생성 및 Order와 연관
        OrderPayment payment = new OrderPayment();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(order.getTotalPrice()); // Order의 총 금액을 결제 금액으로 설정
        payment.setPaymentDate(LocalDateTime.now());

        orderPaymentRepository.save(payment); // Payment 저장

        return order; // 저장된 Order 객체 반환
    }









}

