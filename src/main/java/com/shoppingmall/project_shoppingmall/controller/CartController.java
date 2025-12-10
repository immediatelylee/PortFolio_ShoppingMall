package com.shoppingmall.project_shoppingmall.controller;

import com.fasterxml.jackson.databind.*;
import com.shoppingmall.project_shoppingmall.domain.Member;
import com.shoppingmall.project_shoppingmall.domain.Order;
import com.shoppingmall.project_shoppingmall.dto.*;
import com.shoppingmall.project_shoppingmall.service.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.*;
import javax.validation.*;
import java.security.*;
import java.util.*;
import java.util.stream.*;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ObjectMapper objectMapper;
    private final MemberService memberService;
    private final OrderService orderService;


    /**      장바구니 담기 (AJAX)     */
    //itemdetail (ajax)-> cart  (장바구니 버튼 클릭 -> 장바구니에 담기)
    // CartItemDto는 ItemId, Count만 있는 단순 dto
    @PostMapping(value = "/cart")
    public @ResponseBody ResponseEntity cart(@RequestBody @Valid CartItemDto cartItemDto, BindingResult bindingResult, Principal principal){

        if(bindingResult.hasErrors()){
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("\n"));

            return ResponseEntity.badRequest().body(errorMessage);



        }

        if (principal == null) {
            // 프론트에서 401 체크 후 로그인 페이지로 이동
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 후 이용해주세요.");
        }

        try {
            Long cartItemId = cartService.addCart(cartItemDto, principal.getName());
            return ResponseEntity.ok(cartItemId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body("상품 또는 회원 정보를 찾을 수 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("장바구니 처리 중 오류가 발생했습니다.");
        }

    }

    //사용자정보 이용 카트의 전체 상품정보 로드
    @GetMapping(value = "/cart")
    public String newCart(Principal principal, Model model){

        if (principal == null) {
            return "redirect:/members/login";
        }

        List<CartDetailDto> cartDetailList = cartService.getCartList(principal.getName());
        model.addAttribute("cartItems", cartDetailList);
        return "cart/newCart";
    }



    @PostMapping("/cart/delete/{cartItemId}")
    public String handleDeleteCartItem(@PathVariable Long cartItemId, @RequestParam("_method") String method, RedirectAttributes redirectAttributes) {
        if ("delete".equalsIgnoreCase(method)) {
            try {
                // cartItemId를 이용하여 삭제 로직을 수행합니다.
                cartService.deleteCartItem(cartItemId);
                redirectAttributes.addFlashAttribute("message", "Item successfully deleted.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Failed to delete item.");
            }
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/deleteItems")
    public String deleteCartItems(@RequestBody List<Long> cartItemIds,RedirectAttributes redirectAttributes){
        try{
            cartService.deleteCartItems(cartItemIds);
            redirectAttributes.addFlashAttribute("message", "Item successfully deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete item.");
        }
        return "redirect:/cart";
    }



    // 장바구니 → 주문 생성 (선택/전체 공통)
    @PostMapping("/cart/order")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> orderCartItems(
            @RequestBody OrderRequestDto orderRequestDto,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "로그인이 필요합니다."));
        }

        Member member = memberService.getCurrentMember(principal);

        // 1. 요청으로부터 cartItemId 목록 추출
        List<Long> cartItemIds = orderRequestDto.getItems().stream()
                .map(CartItemOrderDto::getCartItemId)
                .collect(Collectors.toList());

        // 2. 실제 DB 기준 장바구니 정보 조회
        List<CartDetailDto> cartItems =
                cartService.getCartListByIds(principal.getName(), cartItemIds);

        // 3. 화면에서 넘어온 수량 맵
        Map<Long, Integer> viewCountMap = orderRequestDto.getItems().stream()
                .collect(Collectors.toMap(CartItemOrderDto::getCartItemId, CartItemOrderDto::getCount));

        // 4. 화면 수량과 DB 수량이 다르면 CartService를 통해 장바구니 수량 업데이트
        for (CartDetailDto cartItem : cartItems) {
            Integer viewCount = viewCountMap.get(cartItem.getCartItemId());
            if (viewCount != null && cartItem.getCount() != viewCount) {

                // DB 업데이트
                cartService.updateCartItemCount(cartItem.getCartItemId(), viewCount);

                // 주문 생성에 사용할 DTO에도 최신 수량 반영
                cartItem.setCount(viewCount);
            }
        }

        // 5. OrderService에 “최종 수량이 반영된 cartItems”만 넘겨서 주문 생성
        Order order = orderService.createOrderFromCart(member, cartItems);


        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("orderUid", order.getOrderUid());
        result.put("redirectUrl", "/order/checkout/" + order.getOrderUid());

        return ResponseEntity.ok(result);
    }


    //새로만듬.  newCart.html 선택상품주문 ajax에서 /order/selected가기전 전처리과정
    // cartItemId 와 count를 받아서 이전 cart의 count와 확인 상이하다면 수정하고 order로 보내기
    // 세션에 상품 정보와 count를 넣음.
//    @PostMapping("/cart/order")
//    public String orderCartItems(@RequestBody OrderRequestDto orderRequestDto,HttpSession session,Principal principal){
//        if (principal == null) {
//            return "redirect:/members/login";
//        }
//
//        List<Long> cartItemIds = orderRequestDto.getItems().stream()
//                .map(CartItemOrderDto::getCartItemId)
//                .collect(Collectors.toList());
//
//        System.out.println("cartItemIds  " +cartItemIds); // 1 출력됨
//
//        List<CartDetailDto> cartItems = cartService.getCartListByIds(principal.getName(), cartItemIds); //getName : admin@example.com
//        System.out.println("cartItems (cart/order)" +cartItems.toString());
//        if (cartItems != null && !cartItems.isEmpty()) {
//            for (CartDetailDto cartItem : cartItems) {
//                System.out.println("CartItem ID: " + cartItem.getCartItemId());
//                System.out.println("Item Name: " + cartItem.getItemNm());
//                System.out.println("Item Code: " + cartItem.getItemCode());
//                System.out.println("Price: " + cartItem.getPrice());
//                System.out.println("Count: " + cartItem.getCount());
//                System.out.println("Image URL: " + cartItem.getImgUrl());
//                System.out.println("------------------------------");
//            }
//        } else {
//            System.out.println("No items in the cart.");
//        }
//
//
//        Map<Long, Integer> viewCountMap = orderRequestDto.getItems().stream()
//                .collect(Collectors.toMap(CartItemOrderDto::getCartItemId, CartItemOrderDto::getCount));
//
//        for (CartDetailDto cartItem : cartItems) {
//            Integer viewCount = viewCountMap.get(cartItem.getCartItemId()); // get(key) -> value를 출력하므로 count를 출력한다.
//            if (viewCount != null && cartItem.getCount() != viewCount) {
//                cartItem.setCount(viewCount);
//                cartService.updateCartItemCount(cartItem.getCartItemId(), viewCount);
//                System.out.println("test==========================");
//                System.out.println("count다른 부분 수정");
//            }
//        }
//
//        int totalProductPrice = cartItems.stream()
//                .mapToInt(item -> item.getPrice() * item.getCount())
//                .sum();
//
//        int deliveryFee = (totalProductPrice == 0 || totalProductPrice > 50000) ? 0 : 2500;
//        int totalPayPrice = totalProductPrice + deliveryFee;
//
//        // 세션에 저장
//        session.setAttribute("cartItems", cartItems);
//        session.setAttribute("totalProductPrice", totalProductPrice);
//        session.setAttribute("deliveryFee", deliveryFee);
//        session.setAttribute("totalPayPrice", totalPayPrice);
//        session.setAttribute("orderMode","cart");
//        System.out.println("cartItems (session) " +cartItems);
//        System.out.println("totalPayPrice (session)" +totalPayPrice);
//        System.out.println("totalProductPrice (session)" +totalProductPrice);
//        System.out.println("deliveryFee (session)" +deliveryFee);
//
//
//
//        return "redirect:/order/selected";
//    }
}
