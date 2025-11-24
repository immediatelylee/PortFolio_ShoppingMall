package com.shoppingmall.project_shoppingmall.service;

import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import com.shoppingmall.project_shoppingmall.logging.BusinessEventLogger;
import com.shoppingmall.project_shoppingmall.repository.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.thymeleaf.util.*;

import javax.persistence.*;
import java.util.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    private final BusinessEventLogger businessEventLogger;

    /**
     * 장바구니에 상품 추가 (이미 있으면 수량 증가)
     */
    public Long addCart(CartItemDto cartItemDto, String email){

        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new IllegalArgumentException("회원 정보를 찾을 수 없습니다.");
        }

        // 회원별 장바구니 1개 보장
        Cart cart = cartRepository.findByMemberId(member.getId());
        if(cart == null){
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        // Cart + Item 조합으로 기존 CartItem 조회
        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

        int addCount = cartItemDto.getCount();
        int unitPrice = item.getPrice(); // Item.price는 int

        if (savedCartItem != null) {
            // 기존 장바구니 아이템 수량 증가
            savedCartItem.addCount(addCount);

            // 장바구니 수량 증가 이벤트 로그
            businessEventLogger.logAddToCart(
                    member.getId(),
                    item.getId(),
                    addCount,        // 이번에 추가된 수량
                    unitPrice
            );

            return savedCartItem.getId();
        } else {
            CartItem cartItem = CartItem.createCartItem(cart, item, addCount);

            cartItemRepository.save(cartItem);
            // 장바구니 신규 추가 이벤트 로그
            businessEventLogger.logAddToCart(
                    member.getId(),
                    item.getId(),
                    addCount,
                    unitPrice
            );

            return cartItem.getId();
        }
    }


    /**     로그인한 사용자의 장바구니 전체 조회     */
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email){

        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            return Collections.emptyList();
        }

        Cart cart = cartRepository.findByMemberId(member.getId());
        if (cart == null) {
            return Collections.emptyList();
        }

        List<CartDetailDto> cartDetailList =
                cartItemRepository.findCartDetailDtoList(cart.getId());

        // ★ view_cart 이벤트
        if (!cartDetailList.isEmpty()) {
            int itemCount = cartDetailList.stream()
                    .mapToInt(CartDetailDto::getCount)
                    .sum();

            int totalPrice = cartDetailList.stream()
                    .mapToInt(dto -> dto.getPrice() * dto.getCount())
                    .sum();

            businessEventLogger.logViewCart(
                    member.getId(),
                    itemCount,
                    totalPrice
            );
        }
        return  cartDetailList;
    }

    /** 특정 CartItem id 목록으로 장바구니 일부만 조회 (선택 주문용)     */
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartListByIds(String email, List<Long> ids) {


        Member member = memberRepository.findByEmail(email);
        if (member == null || ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        Cart cart = cartRepository.findByMemberId(member.getId());
        if (cart == null) {
            return Collections.emptyList();
        }

        List<CartDetailDto> cartItems =
                cartItemRepository.findCartDetailDtoListByItemIds(cart.getId(), ids);

        // ★ cart_checkout_start 이벤트
        if (!cartItems.isEmpty()) {
            int itemCount = cartItems.stream()
                    .mapToInt(CartDetailDto::getCount)
                    .sum();

            int totalPrice = cartItems.stream()
                    .mapToInt(dto -> dto.getPrice() * dto.getCount())
                    .sum();

            businessEventLogger.logCartCheckoutStart(
                    member.getId(),
                    itemCount,
                    totalPrice
            );
        }

        return cartItems;
    }


    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){

        Member curMember = memberRepository.findByEmail(email);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember();

        return StringUtils.equals(curMember.getEmail(), savedMember.getEmail());
    }

    /** 수량 변경 + update_cart_item 로그 */
    public void updateCartItemCount(Long cartItemId, int count) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        int oldCount = cartItem.getCount();
        if (oldCount == count) {
            return;
        }

        cartItem.updateCount(count);

        Cart cart = cartItem.getCart();
        Member member = cart.getMember();
        Item item = cartItem.getItem();

        if (member != null && item != null) {
            int unitPrice = item.getPrice();

            businessEventLogger.logUpdateCartItem(
                    member.getId(),
                    item.getId(),
                    oldCount,
                    count,
                    unitPrice
            );
        }
    }

    /** 단일 삭제 + remove_from_cart 로그 */
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        Cart cart = cartItem.getCart();
        Member member = cart.getMember();
        Item item = cartItem.getItem();

        int quantity = cartItem.getCount();
        int unitPrice = item.getPrice();

        if (member != null && item != null) {
            businessEventLogger.logRemoveFromCart(
                    member.getId(),
                    item.getId(),
                    quantity,
                    unitPrice
            );
        }

        cartItemRepository.delete(cartItem);
    }

    /** 복수 삭제 + 각각 remove_from_cart 로그 */
    public void deleteCartItems(List<Long> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return;
        }

        List<CartItem> cartItems = cartItemRepository.findAllById(cartItemIds);

        for (CartItem cartItem : cartItems) {
            Cart cart = cartItem.getCart();
            Member member = cart.getMember();
            Item item = cartItem.getItem();

            int quantity = cartItem.getCount();
            int unitPrice = item.getPrice();

            if (member != null && item != null) {
                businessEventLogger.logRemoveFromCart(
                        member.getId(),
                        item.getId(),
                        quantity,
                        unitPrice
                );
            }
        }

        cartItemRepository.deleteAll(cartItems);
    }


    public List<CartItem> getCartItems() {
        return cartItemRepository.findAll();
    }

    public List<CartItem> getCartItemsByIds(List<Long> ids) {
        return cartItemRepository.findAllById(ids);
    }



}
