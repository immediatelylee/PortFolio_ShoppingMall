package com.shoppingmall.project_shoppingmall.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoppingmall.project_shoppingmall.dto.WishlistDetailDto;
import com.shoppingmall.project_shoppingmall.dto.WishlistRequest;
import com.shoppingmall.project_shoppingmall.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;
    private final ObjectMapper objectMapper;

    @GetMapping("/wishlist")
    public String viewWishlist(Principal principal, Model model) {
        List<WishlistDetailDto> wishlistDetailList = wishlistService.getWishlist(principal.getName());
        model.addAttribute("wishlistItems", wishlistDetailList);
        return "wishlist/wishlist";
    }

    @PostMapping(value = "/wishlist")
    public @ResponseBody ResponseEntity addWishlist(@RequestBody @Valid WishlistRequest wishlistRequest, BindingResult bindingResult,
                                                    Principal principal) {

        // 유효성 검사 에러 처리
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName();
        Long wishlistItemId;

        try {
            wishlistItemId = wishlistService.addWishlist(wishlistRequest, email);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(wishlistItemId, HttpStatus.OK);
    }


    // 상품리스트에서 위시리스트 클릭시 삭제 하는 로직
    @PostMapping("/wishlist/remove")
    public ResponseEntity<?> removeItemFromWishlist(@RequestBody WishlistRequest wishlistRequest) {
        try {
            // 아이템 삭제
            wishlistService.deleteByItemId(wishlistRequest.getItemId());

            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("해당 상품이 위시리스트에 없습니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("위시리스트 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 위시리스트 페이지에서 각 로우의 삭제버튼 클릭시 사용
    @PostMapping("/wishlist/delete")
    public ResponseEntity<?> deleteWishlistItem(@RequestBody Map<String, Long> requestData) {
        Long wishlistItemId = requestData.get("wishlistItemId");
        if (wishlistItemId == null) {
            return new ResponseEntity<>("wishlistItemId가 누락되었습니다.", HttpStatus.BAD_REQUEST);
        }

        try {
            // WishlistItemId로 삭제 로직 수행
            wishlistService.deleteWishlistItem(wishlistItemId);
            return new ResponseEntity<>("success", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("해당 항목을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
