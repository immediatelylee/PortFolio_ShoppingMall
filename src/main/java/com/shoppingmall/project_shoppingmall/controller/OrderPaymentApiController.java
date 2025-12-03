package com.shoppingmall.project_shoppingmall.controller;

import com.shoppingmall.project_shoppingmall.dto.PaymentCompleteRequestDto;
import com.shoppingmall.project_shoppingmall.dto.PaymentCompleteResponseDto;
import com.shoppingmall.project_shoppingmall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderPaymentApiController {

    private final OrderService orderService;

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
}
