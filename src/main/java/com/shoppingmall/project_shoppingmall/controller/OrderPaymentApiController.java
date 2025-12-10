package com.shoppingmall.project_shoppingmall.controller;

import com.shoppingmall.project_shoppingmall.domain.Member;
import com.shoppingmall.project_shoppingmall.dto.*;
import com.shoppingmall.project_shoppingmall.service.MemberService;
import com.shoppingmall.project_shoppingmall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class OrderPaymentApiController {

    private final OrderService orderService;
    private final MemberService memberService; // 필요하면 주입

    @PostMapping("/order/complete")
    public ResponseEntity<PaymentCompleteResponseDto> completePayment(
            @RequestBody PaymentCompleteRequestDto requestDto) {

        try {
            orderService.completePayment(requestDto);

            String redirectUrl = "/order/success?orderUid=" + requestDto.getOrderUid();

            PaymentCompleteResponseDto responseDto = new PaymentCompleteResponseDto(
                    true,
                    redirectUrl,
                    "결제 검증 및 주문 확정 완료"
            );
            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            // 로그 찍어두면 좋음
            e.printStackTrace();

            PaymentCompleteResponseDto responseDto = new PaymentCompleteResponseDto(
                    false,
                    null,
                    "결제 검증 실패: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }
    }

    @PostMapping("/order/cancel")
    public ResponseEntity<PaymentCancelResponseDto> cancelOrderBeforePayment(
            @RequestBody PaymentCancelRequestDto requestDto) {

        try {
            orderService.cancelOrderBeforePayment(
                    requestDto.getOrderUid(),
                    requestDto.getReason()
            );

            PaymentCancelResponseDto response = new PaymentCancelResponseDto(
                    true,
                    "주문이 취소되었습니다.",
                    "/cart"   // 취소 후 장바구니로 돌려보냄
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // 로그만 찍고, 프론트에는 에러 메시지 전달
            e.printStackTrace();

            PaymentCancelResponseDto response = new PaymentCancelResponseDto(
                    false,
                    "주문 취소 실패: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/order/fail")
    public ResponseEntity<PaymentFailResponseDto> paymentFail(
            @RequestBody PaymentFailRequestDto dto,
            Principal principal) {

        Long userId = null;
        if (principal != null) {
            Member member = memberService.getCurrentMember(principal);
            if (member != null) {
                userId = member.getId();
            }
        }

        orderService.logPgFailBeforeComplete(dto, userId);

        return ResponseEntity.ok(new PaymentFailResponseDto(true));
    }
}
