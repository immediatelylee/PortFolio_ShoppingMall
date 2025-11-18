package com.shoppingmall.project_shoppingmall.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoppingmall.project_shoppingmall.dto.CartDetailDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TestController {
    private final ObjectMapper objectMapper;

    // objectMapper를 이용한 직렬화가 하도 안되어서 수동으로 직렬화는 되는지 테스트 한 컨트롤러
    // 다행히 잘 된다.

    public TestController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GetMapping("/test/serialize")
    public String testSerialization() {
        List<CartDetailDto> cartItems = new ArrayList<>();
        cartItems.add(new CartDetailDto(1L, "Item 1", "CODE1", 100, 2, "/images/item1.jpg"));
        cartItems.add(new CartDetailDto(2L, "Item 2", "CODE2", 200, 1, "/images/item2.jpg"));

        try {
            // List<CartDetailDto>를 JSON 문자열로 변환
            return objectMapper.writeValueAsString(cartItems);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error serializing cart items";
        }

    }
}
