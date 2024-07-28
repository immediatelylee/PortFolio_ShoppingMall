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
import java.security.*;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;

    private final MemberRepository memberRepository;

    private final OrderRepository orderRepository;

    private final ItemImgRepository itemImgRepository;
    private final MemberService memberService;
//    private final CartService cartService;

    public Long order(OrderDto orderDto, String email){

        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        Member member = memberRepository.findByEmail(email);

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }

//    public Order createOrder(List<Long> cartItemIds, Principal principal) {
//        Member member =memberService.getCurrentMember(principal);
//
//
//        List<CartItem> cartItems;
//        if (cartItemIds == null || cartItemIds.isEmpty()) {
//            cartItems = cartService.getCartItems(); // 모든 장바구니 아이템
//        } else {
//            cartItems = cartService.getCartItemsByIds(cartItemIds); // 선택된 장바구니 아이템
//        }
//
//        if (cartItems.isEmpty()) {
//            throw new IllegalStateException("장바구니가 비어 있습니다.");
//        }
//
//        List<OrderItem> orderItems = new ArrayList<>();
//        for (CartItem cartItem : cartItems) {
//            OrderItem orderItem = OrderItem.createOrderItem(cartItem.getItem(), cartItem.getCount());
//            orderItems.add(orderItem);
//        }
//
//        // 새로운 주문 생성
//        Order order = Order.createOrder(member, orderItems, OrderStatus.ORDER);
//        orderRepository.save(order);
//
//        return order;
//    }

//    public void completeOrder(Long orderId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
//
//        // 주문 완료 처리
//        for (OrderItem orderItem : order.getOrderItems()) {
//            CartItem cartItem = cartService.findCartItemByItemId(orderItem.getItem().getId());
//            if (cartItem != null) {
//                cartService.removeItem(cartItem.getId());
//            }
//        }
//    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {

        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn
                        (orderItem.getItem().getId(), "Y");
                OrderItemDto orderItemDto =
                        new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }

            orderHistDtos.add(orderHistDto);
        }

        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }

    // 로그인한 사용자와 주문 데이터 생성한 사용자가 같은지 검사
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email){
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }

        return true;
    }

    // 주문 취소 상태로 변경하면 변경감지에 의해 update 쿼리 실행
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }

    public Long orders(List<OrderDto> orderDtoList, String email){

        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }

    // 아예 새로 만듬 . 세션을 이용하여 /cart -> /order저장.

    public Order createOrUpdateOrder(List<OrderItem> orderItems, Principal principal, HttpSession session) {
        Member member = memberRepository.findByEmail(principal.getName());


        Order order = (Order) session.getAttribute("order");
        if (order == null || order.getOrderStatus() == OrderStatus.DONE) {
            order = Order.createOrder(member, orderItems);
        } else {
            order.getOrderItems().clear();
            for (OrderItem item : orderItems) {
                order.addOrderItem(item);
            }
        }
        order.setOrderStatus(OrderStatus.ORDER);
        session.setAttribute("order", order);
        return order;
    }

    public Order completeOrder(Principal principal, HttpSession session) {
        Order order = (Order) session.getAttribute("order");
        if (order == null || order.getOrderStatus() == OrderStatus.DONE) {
            throw new IllegalStateException("No active order to complete.");
        }
        order.setOrderStatus(OrderStatus.DONE);
        orderRepository.save(order);
        session.removeAttribute("order");
        return order;
    }

    public Order getCurrentOrder(HttpSession session) {
        return (Order) session.getAttribute("order");
    }
}

