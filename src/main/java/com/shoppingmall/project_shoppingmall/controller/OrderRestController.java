package com.shoppingmall.project_shoppingmall.controller;

import com.shoppingmall.project_shoppingmall.dto.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.*;
import java.util.*;

@RestController
public class OrderRestController {

    @GetMapping("/json/cart-items")
    public List<CartDetailDto> getCartItems(HttpSession session) {
        // 세션에서 cartItems 데이터를 가져옴
        List<CartDetailDto> cartItems = (List<CartDetailDto>) session.getAttribute("cartItems");
        return cartItems;  // JSON으로 자동 직렬화되어 반환됨
    }


}
