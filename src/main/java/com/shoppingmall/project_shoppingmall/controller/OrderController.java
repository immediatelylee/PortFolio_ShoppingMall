package com.shoppingmall.project_shoppingmall.controller;

import com.shoppingmall.project_shoppingmall.domain.Item;
import com.shoppingmall.project_shoppingmall.domain.ItemImg;
import com.shoppingmall.project_shoppingmall.domain.Member;
import com.shoppingmall.project_shoppingmall.domain.Order;
import com.shoppingmall.project_shoppingmall.dto.CartDetailDto;
import com.shoppingmall.project_shoppingmall.dto.OrderItemRequestDto;
import com.shoppingmall.project_shoppingmall.dto.OrderPayRequestDto;
import com.shoppingmall.project_shoppingmall.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;
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
    // selected는 cartController의 /cart/order에 존재함.
    @GetMapping(value = "/order")
    public String orderAllItems(Principal principal, Model model) {
        //사용자 정보(이메일)
        Member member = memberService.getCurrentMember(principal);
        model.addAttribute("UserInfo", member);
        //cart의 전체 상품 조회
        List<CartDetailDto> cartItems = cartService.getCartList(principal.getName());
        model.addAttribute("cartItems", cartItems);

        int totalProductPrice = cartItems.stream()
                .mapToInt(item -> item.getPrice() * item.getCount())
                .sum();

        int deliveryFee = (totalProductPrice == 0) ? 0 : (totalProductPrice > 50000 ? 0 : 2500);
        int totalPayPrice = totalProductPrice + deliveryFee;

        model.addAttribute("totalProductPrice", totalProductPrice);
        model.addAttribute("deliveryFee", deliveryFee);
        model.addAttribute("totalPayPrice", totalPayPrice);
        String itemSummary = generateItemSummary(cartItems);
        model.addAttribute("itemSummary", itemSummary);

        return "order/order";
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
        String orderMode = (String) session.getAttribute("orderMode");
        String itemNm = (String) session.getAttribute("itemNm");
        Integer direct_count = (Integer) session.getAttribute("direct_count");
        String itemCode = (String) session.getAttribute("itemCode");
        ItemImg itemImg = (ItemImg) session.getAttribute("productimg");

        model.addAttribute("direct_itemNm",itemNm);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalProductPrice", totalProductPrice);
        model.addAttribute("deliveryFee", deliveryFee);
        model.addAttribute("totalPayPrice", totalPayPrice);
        model.addAttribute("orderMode",orderMode);
        model.addAttribute("direct_count",direct_count);
        model.addAttribute("itemCode",itemCode);
        model.addAttribute("productimg",itemImg);
        String itemSummary = generateItemSummary(cartItems);

        model.addAttribute("itemSummary", itemSummary);
        System.out.println(itemSummary);

        System.out.println("OrderController CartItems");
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

        return "order/order";
    }
    // ===========================================
    //  결제가 완료된 이후에 order를 생성하는 prework
    // ===========================================
    @PostMapping("/order/prework")
    public ResponseEntity<?> createOrderWithPayment(@RequestBody OrderPayRequestDto orderPayRequestDto) {
        // 1. Order와 OrderItem 생성, OrderPayment 준비
        Order order = orderService.createOrderWithPayment(orderPayRequestDto.getCartDetailDtoList(), orderPayRequestDto.getPaymentMethod());

        // 2. 응답으로 orderUid 반환
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("orderUid", order.getOrderUid());
        response.put("orderName",order.getOrderName());

        return ResponseEntity.ok(response);
//        TODO: 현재 orderPayRequestDto의 paymethod는 ajax에서 card로 지정되어있음. 추후에 수정
    }

    @GetMapping("/order/success")
    public String test(Principal principal, Model model, HttpSession session){
        Member member = memberService.getCurrentMember(principal);
        model.addAttribute("UserInfo", member);

        // 세션에서 데이터 가져오기
        List<CartDetailDto> cartItems = (List<CartDetailDto>) session.getAttribute("cartItems");
        Integer totalProductPrice = (Integer) session.getAttribute("totalProductPrice");
        Integer deliveryFee = (Integer) session.getAttribute("deliveryFee");
        Integer totalPayPrice = (Integer) session.getAttribute("totalPayPrice");
        String orderMode = (String) session.getAttribute("orderMode");
        String itemNm = (String) session.getAttribute("itemNm");
        Integer direct_count = (Integer) session.getAttribute("direct_count");
        String itemCode = (String) session.getAttribute("itemCode");
        ItemImg itemImg = (ItemImg) session.getAttribute("productimg");

        // TODO ItemImg타입으로 그냥 받아왔는데 문제가 생긴다면 처음부터 String타입으로 url받아오도록 수정할 것.
        String productImgurl = itemImg.getImgUrl();


        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalProductPrice", totalProductPrice);
        model.addAttribute("deliveryFee", deliveryFee);
        model.addAttribute("totalPayPrice", totalPayPrice);
        model.addAttribute("productimg",productImgurl);
        model.addAttribute("direct_itemNm",itemNm);
        model.addAttribute("orderMode",orderMode);
        model.addAttribute("direct_count",direct_count);
        model.addAttribute("direct_itemCode",itemCode);



        return "order/orderSuccess";
    }

    @PostMapping("/order/iteminfo")
    public  ResponseEntity<Map<String, Object>> processOrderItemInfo(
            @RequestBody OrderItemRequestDto request,
            HttpSession session) {
        System.out.println("iteminfo start");
        Long itemId = request.getItemId();
        int count = request.getCount();

        // 상품 조회 (없으면 404 응답)
        Item item = itemService.getItemById(itemId);
        if (item == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Item not found"));
        }

        ItemImg itemImg = itemImgService.getByItemId(itemId);

        String itemNm = item.getItemNm();
        int totalProductPrice = (item.getPrice() * count);
        int deliveryFee = (totalProductPrice == 0 || totalProductPrice > 50000) ? 0 : 2500;
        int totalPayPrice = totalProductPrice + deliveryFee;
        String itemCode = item.getItemCode();



        System.out.println("itemId : " + itemId);
        System.out.println("count : " + count);
        System.out.println("totalProductPrice : " + totalProductPrice);
        System.out.println("deliveryFee : "+deliveryFee);
        System.out.println("totalPayPrice : "+ totalPayPrice);
        System.out.println("totalProductPrice : " +totalProductPrice);
        System.out.println("itemCode : " + itemCode);

        session.setAttribute("itemNm", itemNm);
        session.setAttribute("totalProductPrice", totalProductPrice);
        session.setAttribute("deliveryFee", deliveryFee);
        session.setAttribute("totalPayPrice", totalPayPrice);
        session.setAttribute("orderMode","direct");
        session.setAttribute("itemCode",itemCode);
        session.setAttribute("direct_count",count);
        session.setAttribute("itemCode",itemCode);
        session.setAttribute("productimg",itemImg);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("redirectUrl", "/order/selected");
        return ResponseEntity.ok(result);
    }


    private String generateItemSummary(List<CartDetailDto> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return "No items";
        }

        // 아이템 이름을 리스트로 추출
        List<String> itemNames = cartItems.stream()
                .map(CartDetailDto::getItemNm)
                .distinct() // 중복된 아이템 이름을 제거 (필요 시)
                .collect(Collectors.toList());

        // 첫 번째 아이템 이름 가져오기
        String firstItemName = itemNames.get(0);
        int remainingItemCount = itemNames.size() - 1;

        // "사과 외 2건" 또는 "사과"와 같은 형식으로 문자열 생성
        if (remainingItemCount > 0) {
            return firstItemName + " 외 " + remainingItemCount + "건";
        } else {
            return firstItemName;
        }
    }





}
