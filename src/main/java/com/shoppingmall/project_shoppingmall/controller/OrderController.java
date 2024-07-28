package com.shoppingmall.project_shoppingmall.controller;

import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import com.shoppingmall.project_shoppingmall.service.*;
import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.*;

import javax.servlet.http.*;
import javax.validation.*;
import java.security.*;
import java.sql.*;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final CartService cartService;

    // 합쳐서 처리하려다가 ajax랑 뭔가 잘 안맞아서 폐기함. 게속해서 전체 검색만 해서 폐기
//    @GetMapping("/order")
//    public String orderItems(Principal principal, Model model) {
//        Member member = memberService.getCurrentMember(principal);
//        model.addAttribute("UserInfo", member);
//
//        List<Long> cartItemIds = (List<Long>) model.asMap().get("cartItemIds");
//        List<CartDetailDto> cartItems;
//
//        if (cartItemIds == null || cartItemIds.isEmpty()) {
//            // 전체 상품 조회
//            cartItems = cartService.getCartList(principal.getName());
//        } else {
//            // 선택된 상품 조회
//            cartItems = cartService.getCartListByIds(principal.getName(), cartItemIds);
//        }
//
//        model.addAttribute("cartItems", cartItems);
//
//        int totalProductPrice = cartItems.stream()
//                .mapToInt(item -> item.getPrice() * item.getCount())
//                .sum();
//
//        int deliveryFee = totalProductPrice > 50000 ? 0 : 2500;
//        int totalPayPrice = totalProductPrice + deliveryFee;
//
//        model.addAttribute("totalProductPrice", totalProductPrice);
//        model.addAttribute("deliveryFee", deliveryFee);
//        model.addAttribute("totalPayPrice", totalPayPrice);
//
//        return "order/Order";
//    }

    // 세션에 저장하지 않고 바로 하는 전체 상품주문형태
    // selected는 cartController의 /cart/order에 존재함.
    @GetMapping(value = "/order")
    public String orderAllItems(Principal principal, Model model) {
        Member member = memberService.getCurrentMember(principal);
        model.addAttribute("UserInfo", member);

        List<CartDetailDto> cartItems = cartService.getCartList(principal.getName());
        model.addAttribute("cartItems", cartItems);

        int totalProductPrice = cartItems.stream()
                .mapToInt(item -> item.getPrice() * item.getCount())
                .sum();

        int deliveryFee = totalProductPrice > 50000 ? 0 : 2500;
        int totalPayPrice = totalProductPrice + deliveryFee;

        model.addAttribute("totalProductPrice", totalProductPrice);
        model.addAttribute("deliveryFee", deliveryFee);
        model.addAttribute("totalPayPrice", totalPayPrice);

        return "order/Order";
    }

    @GetMapping("/order/selected")
    public String orderSelectedItemsGet(Principal principal, Model model, HttpSession session) {
        Member member = memberService.getCurrentMember(principal);
        model.addAttribute("UserInfo", member);

        // 세션에서 데이터 가져오기
        List<CartDetailDto> cartItems = (List<CartDetailDto>) session.getAttribute("cartItems");
        Integer totalProductPrice = (Integer) session.getAttribute("totalProductPrice");
        Integer deliveryFee = (Integer) session.getAttribute("deliveryFee");
        Integer totalPayPrice = (Integer) session.getAttribute("totalPayPrice");

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalProductPrice", totalProductPrice);
        model.addAttribute("deliveryFee", deliveryFee);
        model.addAttribute("totalPayPrice", totalPayPrice);

        return "order/Order";
    }



    //수정 전
//    @GetMapping("/order/selected")
//    public String orderSelectedItemsGet(Principal principal, Model model, IdsTransferDto idsTransferDto) {
//        Member member = memberService.getCurrentMember(principal);
//        model.addAttribute("UserInfo", member);
//
//
//        List<Long> cartItemIds = idsTransferDto.getSelectedIds();
//
//        if (cartItemIds != null && !cartItemIds.isEmpty()) {
//            List<CartDetailDto> cartItems = cartService.getCartListByIds(member.getName(), cartItemIds);
//
//            int totalProductPrice = cartItems.stream()
//                    .mapToInt(item -> item.getPrice() * item.getCount())
//                    .sum();
//
//            int deliveryFee = totalProductPrice > 50000 ? 0 : 2500;
//            int totalPayPrice = totalProductPrice + deliveryFee;
//
//            model.addAttribute("cartItems", cartItems);
//            model.addAttribute("totalProductPrice", totalProductPrice);
//            model.addAttribute("deliveryFee", deliveryFee);
//            model.addAttribute("totalPayPrice", totalPayPrice);
//        } else {
//            // handle the case when cartItemIds is null or empty
//            model.addAttribute("cartItems", Collections.emptyList());
//            model.addAttribute("totalProductPrice", 0);
//            model.addAttribute("deliveryFee", 0);
//            model.addAttribute("totalPayPrice", 0);
//        }
//
//        return "order/Order";
//    }


    //임시
//    @PostMapping("/order_id_save")
//    public ResponseEntity<Map<String, String>> saveOrderIds(@RequestBody List<Long> ids, RedirectAttributes redirectAttributes) {
//        // debug 로그 추가
//        System.out.println("Received IDs: " + ids);
//
//        if (ids == null || ids.isEmpty()) {
//            // ids가 없을 경우에 대한 예외 처리
//            return ResponseEntity.badRequest().body(Collections.singletonMap("redirectUrl", "/order"));
//        }
//
//        // 플래시 속성에 IDs 추가
//        redirectAttributes.addFlashAttribute("cartItemIds", ids);
//
//        // 리다이렉트 URL 반환
//        return ResponseEntity.ok(Collections.singletonMap("redirectUrl", "/order"));
//    }


    //ajax에서 선택상품주문을 했을때 선택상품에 대한 id를 redirectAttribute에 저장하는 과정-다시
//    @PostMapping("/order_id_save")
//    public String saveOrderIds(@RequestBody List<Long> ids, RedirectAttributes redirectAttributes) {
//        // debug 로그 추가
//        System.out.println("Received IDs: " + ids);
//
//        if (ids == null || ids.isEmpty()) {
//            // ids가 없을 경우에 대한 예외 처리
//            return "redirect:/order"; // 리다이렉트하여 GET 요청으로 처리
//        }
//
//        // 플래시 속성에 IDs 추가
//        redirectAttributes.addFlashAttribute("cartItemIds", ids);
//
//        // 리다이렉트
//        return "redirect:/order";
//    }

//    @PostMapping(value = "/orders")
//    public String orderSelectedItems(Principal principal, Model model, @RequestBody List<Long> ids) {
//        // debug 로그 추가
//        System.out.println("Received IDs: " + ids);
//
//        Member member = memberService.getCurrentMember(principal);
//        model.addAttribute("UserInfo", member);
//
//        List<CartDetailDto> cartItems = cartService.getCartListByIds(principal.getName(), ids);
//        model.addAttribute("cartItems", cartItems);
//
//        int totalProductPrice = cartItems.stream()
//                .mapToInt(item -> item.getPrice() * item.getCount())
//                .sum();
//
//        int deliveryFee = totalProductPrice > 50000 ? 0 : 2500;
//        int totalPayPrice = totalProductPrice + deliveryFee;
//
//        model.addAttribute("totalProductPrice", totalProductPrice);
//        model.addAttribute("deliveryFee", deliveryFee);
//        model.addAttribute("totalPayPrice", totalPayPrice);
//
//        return "order/Order";
//    }

//    @GetMapping(value = "/order")
//    public String orderTest(Principal principal ,Model model, @RequestParam(required = false) List<Long> ids){
//        Member member =memberService.getCurrentMember(principal);
//        model.addAttribute("UserInfo",member);
//
//        List<CartDetailDto> cartItems;
//
//        if (ids == null || ids.isEmpty()) {
//            cartItems = cartService.getCartList(principal.getName());
//
//        } else{
//            cartItems = cartService.getCartListByIds(principal.getName(), ids);
//
//        }
//        model.addAttribute("cartItems", cartItems);
//        int totalProductPrice = cartItems.stream()
//                .mapToInt(item -> item.getPrice() * item.getCount())
//                .sum();
//
//        int deliveryFee = totalProductPrice > 50000 ? 0 : 2500;
//        int totalPayPrice = totalProductPrice + deliveryFee;
//
//        model.addAttribute("totalProductPrice", totalProductPrice);
//        model.addAttribute("deliveryFee", deliveryFee);
//        model.addAttribute("totalPayPrice", totalPayPrice);
//
//        return "order/Order";
//    }






//    @PostMapping("/order/checkout")
//    public String placeOrder(@RequestBody List<Long> cartItemIds, Principal principal, Model model) {
//        Member member = memberService.getCurrentMember(principal);
//
//        // 선택된 상품만 조회
//        List<CartDetailDto> selectedCartItems = cartService.getSelectedCartItems(principal.getName(), cartItemIds);
//
//        // 주문 처리 로직 구현
//        int totalProductPrice = selectedCartItems.stream()
//                .mapToInt(item -> item.getPrice() * item.getCount())
//                .sum();
//
//        int deliveryFee = totalProductPrice > 50000 ? 0 : 2500;
//        int totalPayPrice = totalProductPrice + deliveryFee;
//
//        // 모델에 데이터 추가
//        model.addAttribute("UserInfo", member);
//        model.addAttribute("cartItems", selectedCartItems);
//        model.addAttribute("totalProductPrice", totalProductPrice);
//        model.addAttribute("deliveryFee", deliveryFee);
//        model.addAttribute("totalPayPrice", totalPayPrice);
//
//        // 주문 페이지로 이동
//        return "order/Order";
//    }

    // 새로만듬.
    @PostMapping("/createOrUpdate")
    public ResponseEntity<Order> createOrUpdateOrder(@RequestBody List<OrderItem> orderItems, Principal principal, HttpSession session) {
        Order updatedOrder = orderService.createOrUpdateOrder(orderItems, principal, session);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/complete")
    public ResponseEntity<Order> completeOrder(Principal principal, HttpSession session) {
        try {
            Order completedOrder = orderService.completeOrder(principal, session);
            return ResponseEntity.ok(completedOrder);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @GetMapping("/current")
    public ResponseEntity<Order> getCurrentOrder(HttpSession session) {
        Order currentOrder = orderService.getCurrentOrder(session);
        if (currentOrder == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(currentOrder);
    }




}
