package com.shoppingmall.project_shoppingmall.controller;

import com.shoppingmall.project_shoppingmall.dto.*;
import com.shoppingmall.project_shoppingmall.service.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.*;

import javax.validation.*;
import java.security.*;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;


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

    @GetMapping(value = "/cart")
    public String orderHist(Principal principal, Model model){
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

    // 주문
    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto, Principal principal){

    List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();

    if(cartOrderDtoList == null || cartOrderDtoList.size() == 0){
        return new ResponseEntity<String>("주문할 상품을 선택해주세요", HttpStatus.FORBIDDEN);
    }

    for (CartOrderDto cartOrder : cartOrderDtoList) {
        if(!cartService.validateCartItem(cartOrder.getCartItemId(), principal.getName())){
            return new ResponseEntity<String>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
    }

    Long orderId = cartService.orderCartItem(cartOrderDtoList, principal.getName());
    return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }
}
