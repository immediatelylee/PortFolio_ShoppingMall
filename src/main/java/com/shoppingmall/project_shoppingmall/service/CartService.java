package com.shoppingmall.project_shoppingmall.service;

import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
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

        if(savedCartItem != null){
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        } else {
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);
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

        return  cartItemRepository.findCartDetailDtoList(cart.getId());
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

        return cartItemRepository.findCartDetailDtoListByItemIds(cart.getId(), ids);
    }


    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){

        Member curMember = memberRepository.findByEmail(email);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }

        return true;
    }

    public void updateCartItemCount(Long cartItemId, int count){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }

    //삭제기능
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

    public void deleteCartItems(List<Long> cartItemIds){
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return;
        }
        List<CartItem> cartItems = cartItemRepository.findAllById(cartItemIds);

        cartItemRepository.deleteAll(cartItems);
    }


    public List<CartItem> getCartItems() {
        return cartItemRepository.findAll();
    }

    public List<CartItem> getCartItemsByIds(List<Long> ids) {
        return cartItemRepository.findAllById(ids);
    }




}
