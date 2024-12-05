package com.shoppingmall.project_shoppingmall.service;

import com.shoppingmall.project_shoppingmall.domain.Item;
import com.shoppingmall.project_shoppingmall.domain.Member;
import com.shoppingmall.project_shoppingmall.domain.Wishlist;
import com.shoppingmall.project_shoppingmall.domain.WishlistItem;
import com.shoppingmall.project_shoppingmall.dto.CartItemDto;
import com.shoppingmall.project_shoppingmall.dto.WishlistDetailDto;
import com.shoppingmall.project_shoppingmall.dto.WishlistRequest;
import com.shoppingmall.project_shoppingmall.repository.ItemRepository;
import com.shoppingmall.project_shoppingmall.repository.MemberRepository;
import com.shoppingmall.project_shoppingmall.repository.WishlistItemRepository;
import com.shoppingmall.project_shoppingmall.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;

    public Long addWishlist(WishlistRequest wishlistRequest, String email){
        Item item = itemRepository.findById(wishlistRequest.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);

        Wishlist wishlist = wishlistRepository.findByMemberId(member.getId());
        if(wishlist == null){
            wishlist = Wishlist.createWishlist(member);
            wishlistRepository.save(wishlist);
        }

        WishlistItem savedWishlistItem = wishlistItemRepository.findByWishlistIdAndItemId(wishlist.getId(),item.getId());

        if(savedWishlistItem != null){
            throw new IllegalStateException("이미 위시리스트에 존재하는 상품입니다.");
        }
        //cart와 다르게 count넣는 부분이 빠져있음

        WishlistItem wishlistItem = WishlistItem.createWishlistItem(wishlist, item);
        wishlistItemRepository.save(wishlistItem);

        return wishlistItem.getId();

    }
    // 사용자의 모든 Wishlist조회
    @Transactional(readOnly = true)
    public List<WishlistDetailDto> getWishlist(String email) {

        List<WishlistDetailDto> wishlistDetailDtoList = new ArrayList<>();

        // 1. 회원 조회
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            return wishlistDetailDtoList; // 회원이 없으면 빈 리스트 반환
        }

        // 2. 회원의 위시리스트 조회
        Wishlist wishlist = wishlistRepository.findByMemberId(member.getId());
        if (wishlist == null) {
            return wishlistDetailDtoList; // 위시리스트가 없으면 빈 리스트 반환
        }

        // 3. 위시리스트에 포함된 상품 정보를 조회
        wishlistDetailDtoList = wishlistItemRepository.findWishlistDetailDtoList(wishlist.getId());
        return wishlistDetailDtoList;
    }

    // member 이메일 기반으로 위시리스트 아이템 ID를 조회 이후 위시리스트 등록 - 상품리스트에서 하트 클릭하여 등록
    public List<Long> getWishlistItemIds(String memberEmail) {
        return wishlistItemRepository.findItemIdsByMemberEmail(memberEmail);
    }

    // itemId를 기반으로 WishlistItem 삭제 - 상품리스트에서 하트 클릭으로 삭제
    public void deleteByItemId(Long itemId) {
        // itemId로 WishlistItem 조회
        WishlistItem wishlistItem = wishlistItemRepository.findByItemId(itemId)
                .orElseThrow(() -> new EntityNotFoundException("해당 itemId와 일치하는 WishlistItem이 없습니다."));

        // 삭제
        wishlistItemRepository.delete(wishlistItem);
    }

    // wishlist내에서 삭제
    public void deleteWishlistItem(Long wishlistItemId) {
        WishlistItem wishlistItem = wishlistItemRepository.findById(wishlistItemId)
                .orElseThrow(EntityNotFoundException::new);
        wishlistItemRepository.delete(wishlistItem);
    }
    //wishlist내에서 삭제기능
    public void deleteWishlistItems(List<Long> wishlistItemIds) {
        List<WishlistItem> wishlistItems = wishlistItemRepository.findAllById(wishlistItemIds);
        wishlistItemRepository.deleteAll(wishlistItems);
    }





}
