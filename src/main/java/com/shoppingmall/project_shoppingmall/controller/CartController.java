package com.shoppingmall.project_shoppingmall.controller;

import com.fasterxml.jackson.databind.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import com.shoppingmall.project_shoppingmall.service.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.*;

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

    //itemdetail (ajax)-> cart  (장바구니 버튼 클릭 -> 장바구니에 담기)
    @PostMapping(value = "/cart")
    public @ResponseBody ResponseEntity cart(@RequestBody @Valid CartItemDto cartItemDto, BindingResult bindingResult, Principal principal){

        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName();
        Long cartItemId;

        try {
            cartItemId = cartService.addCart(cartItemDto, email);
        } catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    //사용자정보 이용 카트의 전체 상품정보 로드
    @GetMapping(value = "/cart")
    public String newCart(Principal principal, Model model){
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



    //새로만듬.  newCart.html 선택상품주문 ajax에서 /order/selected가기전 전처리과정
    // cartItemId 와 count를 받아서 이전 cart의 count와 확인 상이하다면 수정하고 order로 보내기
    // 세션에 상품 정보와 count를 넣음.
    @PostMapping("/cart/order")
    public String orderCartItems(@RequestBody OrderRequestDto orderRequestDto,HttpSession session,Principal principal){
        List<Long> cartItemIds = orderRequestDto.getItems().stream()
                .map(CartItemOrderDto::getCartItemId)
                .collect(Collectors.toList());
        System.out.println("cartItemIds  " +cartItemIds); // 1 출력됨

        List<CartDetailDto> cartItems = cartService.getCartListByIds(principal.getName(), cartItemIds); //getName : admin@example.com
        System.out.println("cartItems (cart/order)" +cartItems.toString());
        if (cartItems != null && !cartItems.isEmpty()) {
            for (CartDetailDto cartItem : cartItems) {
                System.out.println("CartItem ID: " + cartItem.getCartItemId());
                System.out.println("Item Name: " + cartItem.getItemNm());
                System.out.println("Item Code: " + cartItem.getItemCode());
                System.out.println("Price: " + cartItem.getPrice());
                System.out.println("Count: " + cartItem.getCount());
                System.out.println("Image URL: " + cartItem.getImgUrl());
                System.out.println("------------------------------");
            }
        } else {
            System.out.println("No items in the cart.");
        }


        Map<Long, Integer> viewCountMap = orderRequestDto.getItems().stream()
                .collect(Collectors.toMap(CartItemOrderDto::getCartItemId, CartItemOrderDto::getCount));

        for (CartDetailDto cartItem : cartItems) {
            Integer viewCount = viewCountMap.get(cartItem.getCartItemId()); // get(key) -> value를 출력하므로 count를 출력한다.
            if (viewCount != null && cartItem.getCount() != viewCount) {
                cartItem.setCount(viewCount);
                cartService.updateCartItemCount(cartItem.getCartItemId(), viewCount);
                System.out.println("test==========================");
                System.out.println("count다른 부분 수정");
            }
        }

        int totalProductPrice = cartItems.stream()
                .mapToInt(item -> item.getPrice() * item.getCount())
                .sum();

        int deliveryFee = (totalProductPrice == 0 || totalProductPrice > 50000) ? 0 : 2500;
        int totalPayPrice = totalProductPrice + deliveryFee;

        // 세션에 저장
        session.setAttribute("cartItems", cartItems);
        session.setAttribute("totalProductPrice", totalProductPrice);
        session.setAttribute("deliveryFee", deliveryFee);
        session.setAttribute("totalPayPrice", totalPayPrice);
        System.out.println("cartItems (session) " +cartItems);
        System.out.println("totalPayPrice (session)" +totalPayPrice);
        System.out.println("totalProductPrice (session)" +totalProductPrice);
        System.out.println("deliveryFee (session)" +deliveryFee);



        return "redirect:/order/selected";
    }
}
