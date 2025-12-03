package com.shoppingmall.project_shoppingmall.controller;

import com.shoppingmall.project_shoppingmall.domain.Item;
import com.shoppingmall.project_shoppingmall.domain.ItemImg;
import com.shoppingmall.project_shoppingmall.domain.Member;
import com.shoppingmall.project_shoppingmall.domain.Order;
import com.shoppingmall.project_shoppingmall.dto.CartDetailDto;
import com.shoppingmall.project_shoppingmall.dto.DirectOrderRequestDto;
import com.shoppingmall.project_shoppingmall.dto.OrderItemRequestDto;
import com.shoppingmall.project_shoppingmall.dto.OrderPayRequestDto;
import com.shoppingmall.project_shoppingmall.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final CartService cartService;
    private final ItemService itemService;
    private final ItemImgService itemImgService;

    // 세션에 저장하지 않고 바로 하는 전체 상품주문형태
    // 1) 상품 상세에서 바로구매
    @PostMapping("/order/direct")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> directOrder(
            @RequestBody DirectOrderRequestDto request,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "로그인이 필요합니다."));
        }

        Member member = memberService.getCurrentMember(principal);

        Order order = orderService.createDirectOrder(member, request.getItemId(), request.getCount());

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("orderUid", order.getOrderUid());
        result.put("redirectUrl", "/order/checkout/" + order.getOrderUid());

        return ResponseEntity.ok(result);
    }

    // 2) 주문서 페이지
    @GetMapping("/order/checkout/{orderUid}")
    public String checkout(@PathVariable String orderUid,
                           Principal principal,
                           Model model) {

        Member member = memberService.getCurrentMember(principal);
        Order order = orderService.getOrderWithItems(orderUid);

        model.addAttribute("UserInfo", member);
        model.addAttribute("order", order);
        model.addAttribute("orderItems", order.getOrderItems());
//        model.addAttribute("totalProductPrice", order.getTotalPrice());

        BigDecimal totalProductPrice = order.getTotalPrice();

        int deliveryFee = order.getTotalPrice().compareTo(BigDecimal.valueOf(50000)) > 0
                ? 0 : 2500;
        BigDecimal totalPay = order.getTotalPrice().add(BigDecimal.valueOf(deliveryFee));

        model.addAttribute("totalProductPrice", totalProductPrice);
        model.addAttribute("deliveryFee", deliveryFee);
        model.addAttribute("totalPayPrice", totalPay);

        return "order/order"; // 기존 order/order.html 재사용
    }

    // 3) 결제 완료 콜백 (PG에서 성공 후)
    @PostMapping("/order/pay")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> pay(
            @RequestBody OrderPayRequestDto dto) {

        orderService.completePayment(
                dto.getOrderUid(),
                dto.getPaymentMethod(),
                dto.getAmount(),
                dto.getPayKey(),
                dto.getPgTid()
        );

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("redirectUrl", "/order/success/" + dto.getOrderUid());

        return ResponseEntity.ok(result);
    }

    // 4) 결제 성공 페이지
    @GetMapping("/order/success/{orderUid}")
    public String success(@PathVariable String orderUid,
                          Principal principal,
                          Model model) {

        Member member = memberService.getCurrentMember(principal);
        Order order = orderService.getOrderWithItems(orderUid);

        model.addAttribute("UserInfo", member);
        model.addAttribute("order", order);
        model.addAttribute("orderItems", order.getOrderItems());
        model.addAttribute("orderPayment", order.getOrderPayment());
        model.addAttribute("totalProductPrice", order.getTotalPrice());

        int deliveryFee = order.getTotalPrice().compareTo(BigDecimal.valueOf(50000)) > 0
                ? 0 : 2500;
        BigDecimal totalPay = order.getTotalPrice().add(BigDecimal.valueOf(deliveryFee));

        model.addAttribute("deliveryFee", deliveryFee);
        model.addAttribute("totalPayPrice", totalPay);

        return "order/orderSuccess";
    }




}
