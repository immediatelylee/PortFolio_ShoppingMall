package com.shoppingmall.project_shoppingmall.controller;

import com.shoppingmall.project_shoppingmall.dto.IamportWebhookRequestDto;
import com.shoppingmall.project_shoppingmall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class IamportWebhookController {

    private final OrderService orderService;

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody IamportWebhookRequestDto dto) {

        // TODO: Iamport에서 오는 시크릿 검증 등 보안 체크

        // ex) status = "cancelled" 인 경우만 처리
        if ("cancelled".equals(dto.getStatus())) {
            orderService.handlePgCancelledPayment(dto);
        }

        return ResponseEntity.ok().build();
    }
}

