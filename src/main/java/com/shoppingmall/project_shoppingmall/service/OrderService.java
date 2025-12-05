package com.shoppingmall.project_shoppingmall.service;

import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import com.shoppingmall.project_shoppingmall.logging.BusinessEventLogger;
import com.shoppingmall.project_shoppingmall.repository.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.persistence.*;
import javax.servlet.http.*;
import java.math.*;
import java.security.*;
import java.time.*;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderPaymentRepository orderPaymentRepository;
    private final ItemService itemService;
    private final IamportClientService iamportClientService;

    private final BusinessEventLogger businessEventLogger;

    // 1) 장바구니 기반 주문 생성
    public Order createOrderFromCart(Member member, List<CartDetailDto> cartItems) {

        String orderUid = generateOrderUid();
        Order order = Order.builder()
                .member(member)
                .orderUid(orderUid)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING)
                .build();

        for (CartDetailDto cartItem : cartItems) {

            OrderItem orderItem = OrderItem.builder()
                    .productName(cartItem.getItemNm())
                    .price(BigDecimal.valueOf(cartItem.getPrice()))
                    .quantity(cartItem.getCount()) //  여기서는 이미 최종 count가 들어있다고 가정
                    .imageUrl(cartItem.getImgUrl())
                    .order(order)
                    .build();

            order.addOrderItem(orderItem);
        }

        orderRepository.save(order); // cascade로 OrderItem까지 저장

        // 🔹 비즈니스 로그: 주문 생성
        int itemCount = order.getOrderItems().size();
        int totalAmount = calculateExpectedPayAmount(order).intValue(); // BigDecimal → int (원 단위)

        businessEventLogger.logOrderCreated(
                member.getId(),          // userId
                order.getId(),           // orderId
                totalAmount,             // totalAmountInWon
                itemCount,               // itemCount
                "UNKNOWN"                // paymentMethod (아직 결제수단 미정)
        );

        return order;
    }

    // 2) 상품 상세에서 바로구매
    public Order createDirectOrder(Member member, Long itemId, int count) {
        Item item = itemService.getItemById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Item not found: " + itemId);
        }


        String orderUid = generateOrderUid();
        Order order = Order.builder()
                .member(member)
                .orderUid(orderUid)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING)
                .build();

        OrderItem orderItem = OrderItem.builder()
                .productName(item.getItemNm())
                .price(BigDecimal.valueOf(item.getPrice()))
                .quantity(count)
                .build();

        order.addOrderItem(orderItem);

        orderRepository.save(order);

        // 비즈니스 로그: 주문 생성
        int itemCount = order.getOrderItems().size();
        int totalAmount = calculateExpectedPayAmount(order).intValue();

        businessEventLogger.logOrderCreated(
                member.getId(),
                order.getId(),
                totalAmount,
                itemCount,
                "UNKNOWN"    // 아직 결제수단 모름
        );

        return order;
    }

    @Transactional(readOnly = true)
    public Order getOrderByUid(String orderUid) {
        return orderRepository.findByOrderUid(orderUid)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderUid));
    }

    public void completePayment(String orderUid,
                                String paymentMethod,
                                BigDecimal paidAmount,
                                String payKey,
                                String pgTid) {

        Order order = orderRepository.findByOrderUid(orderUid)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderUid));

        BigDecimal orderTotal = order.getTotalPrice();

        if (orderTotal.compareTo(paidAmount) != 0) {

            OrderPayment payment = OrderPayment.builder()
                    .order(order)
                    .paymentMethod(paymentMethod)
                    .amount(paidAmount)
                    .paymentDate(LocalDateTime.now())
                    .paymentStatus(PaymentStatus.FAILED)
                    .payKey(payKey)
                    .pgTid(pgTid)
                    .build();

            orderPaymentRepository.save(payment);
            order.markFailed();
            return;
        }

        OrderPayment payment = OrderPayment.builder()
                .order(order)
                .paymentMethod(paymentMethod)
                .amount(paidAmount)
                .paymentDate(LocalDateTime.now())
                .paymentStatus(PaymentStatus.SUCCESS)
                .payKey(payKey)
                .pgTid(pgTid)
                .build();

        orderPaymentRepository.save(payment);
        order.markPaid();
    }
    @Transactional(readOnly = true)
    public Order getOrderWithItems(String orderUid) {
        return orderRepository.findByOrderUidWithItems(orderUid)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderUid));
    }


    /**
     * iamport 결제 검증 및 Order / OrderPayment 확정
     */
    @Transactional
    public void completePayment(PaymentCompleteRequestDto dto) {

        log.info(">>> [DTO COMPLETE] start, orderUid={}", dto.getOrderUid());

        Order order = null;
        Integer amountFromPg = null;          // PG에서 조회한 금액
        String paymentMethodForLog = null;    // 로그용 결제수단
        Long paymentIdForLog = null;          // 저장된 OrderPayment PK

        try {
            // 1) DB에서 Order 조회
            order = orderRepository.findByOrderUid(dto.getOrderUid())
                    .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));

            // 2) Iamport 토큰 발급
            String accessToken = iamportClientService.getAccessToken();

            // 3) imp_uid로 결제 정보 조회
            Map<String, Object> paymentData = iamportClientService.getPaymentData(dto.getImpUid(), accessToken);

            amountFromPg = (Integer) paymentData.get("amount");    // ex) 13500
            String status = (String) paymentData.get("status");    // paid, ready, cancelled 등
            String merchantUidFromPg = (String) paymentData.get("merchant_uid");

            // 🔍 디버깅 로그
            log.info(">>> [PG] merchant_uid   = {}", merchantUidFromPg);
            log.info(">>> [DB] order.orderUid = {}", order.getOrderUid());
            log.info(">>> [PG] amount         = {}", amountFromPg);
            log.info(">>> [DB] totalPrice     = {}", order.getTotalPrice());

            BigDecimal expectedAmount = calculateExpectedPayAmount(order);
            BigDecimal pgAmount = BigDecimal.valueOf(amountFromPg.longValue());

            // 4) 주문번호 검증
            if (!merchantUidFromPg.equals(order.getOrderUid())) {

                businessEventLogger.logPaymentCompleted(
                        order.getOrderUid(),                                  // orderUid
                        order.getMember() != null ? order.getMember().getId() : null, // userId
                        pgAmount.intValue(),                                  // amountInWon
                        false,                                                // success
                        "UNKNOWN",                                            // paymentMethod
                        PaymentStatus.FAILED.name(),                          // paymentStatus
                        order.getOrderStatus().name(),                        // orderStatus (PENDING)
                        null,                                                 // paymentId
                        "merchant_uid mismatch"                               // cancelReason
                );
                throw new IllegalStateException("주문번호(merchant_uid)가 일치하지 않습니다.");
            }

            // 5) 결제 금액 검증 (배송비 포함 총 결제금액으로 비교했다면 그 기준에 맞춰서)
            if (expectedAmount.compareTo(pgAmount) != 0) {
                businessEventLogger.logPaymentCompleted(
                        order.getOrderUid(),
                        order.getMember() != null ? order.getMember().getId() : null,
                        pgAmount.intValue(),
                        false,
                        "UNKNOWN",
                        PaymentStatus.FAILED.name(),
                        order.getOrderStatus().name(),        // 아직 PENDING
                        null,
                        "amount mismatch"
                );
                throw new IllegalStateException("결제 금액이 일치하지 않습니다.");
            }

            // 서버에서 "기대하는 결제 금액" (상품합 + 배송비)


            log.info(">>> [CHECK] expectedAmount = {}", expectedAmount);
            log.info(">>> [CHECK] pgAmount       = {}", pgAmount);

            if (expectedAmount.compareTo(pgAmount) != 0) {
                throw new IllegalStateException("결제 금액이 일치하지 않습니다.");
            }

            // 6) 결제 상태 검증
            if (!"paid".equals(status)) {
                businessEventLogger.logPaymentCompleted(
                        order.getOrderUid(),
                        order.getMember() != null ? order.getMember().getId() : null,
                        pgAmount.intValue(),
                        false,
                        "UNKNOWN",
                        PaymentStatus.FAILED.name(),
                        order.getOrderStatus().name(),
                        null,
                        "status=" + status
                );
                throw new IllegalStateException("결제 상태가 완료(paid)가 아닙니다. 상태=" + status);
            }

            // 7) 여기까지 통과하면 결제 정상 → 주문/결제 상태 확정
            order.setOrderStatus(OrderStatus.PAID);

            OrderPayment payment = order.getOrderPayment();
            if (payment == null) {
                payment = new OrderPayment();
                payment.setOrder(order);
            }

            // TODO: 필요하면 paymentData 에서 실제 pay_method 읽어서 세팅
            payment.setPaymentMethod("CARD");
            payment.setAmount(expectedAmount);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setPaymentStatus(PaymentStatus.SUCCESS);

            paymentMethodForLog = payment.getPaymentMethod();

            orderPaymentRepository.save(payment);
            orderRepository.save(order);

            paymentIdForLog = payment.getId();

            // ✅ 결제 성공 비즈니스 로그
            businessEventLogger.logPaymentCompleted(
                    order.getOrderUid(),                  // orderUid (merchant_uid)
                    order.getMember().getId(),            // userId
                    amountFromPg,                         // amountInWon
                    true,                                 // success
                    payment.getPaymentMethod(),           // paymentMethod
                    payment.getPaymentStatus().name(),    // paymentStatus = SUCCESS
                    order.getOrderStatus().name(),        // orderStatus = PAID
                    payment.getId(),                      // paymentId
                    null                                  // cancelReason 없음
            );

        } catch (Exception e) {

            log.warn(">>> 결제 검증/완료 처리 중 예외 발생: {}", e.getMessage(), e);

            // ❌ 주문 상태를 FAILED 로 마킹 (원하면 여기서 CANCELLED 로 바꾸는 정책도 가능)
            if (order != null) {
                order.setOrderStatus(OrderStatus.FAILED);
                orderRepository.save(order);
            }

            // ❌ 결제 실패/취소 실패 비즈니스 로그 (취소 실패도 여기 패턴 재사용)
            try {
                businessEventLogger.logPaymentCompleted(
                        dto.getOrderUid(),                                         // orderUid (요청 기준)
                        (order != null && order.getMember() != null)
                                ? order.getMember().getId()
                                : null,                                           // userId (없으면 null)
                        amountFromPg != null ? amountFromPg : dto.getPaidAmount(),// amountInWon (PG 금액 또는 요청 금액)
                        false,                                                    // success = false
                        paymentMethodForLog,                                      // 결제수단 (알 수 없으면 null)
                        PaymentStatus.FAILED.name(),                              // paymentStatus = FAILED
                        (order != null) ? order.getOrderStatus().name() : null,   // orderStatus = FAILED or null
                        paymentIdForLog,                                          // 결제 PK (저장 안됐으면 null)
                        e.getMessage()                                            // cancelReason / 실패 사유
                );
            } catch (Exception logEx) {
                log.warn("payment_completed 비즈니스 로그 기록 실패", logEx);
            }

            // 컨트롤러 쪽에 그대로 예외 전달
            throw e;
        }
    }


    private String generateOrderUid() {
        String uuid = UUID.randomUUID().toString().replace("-", ""); // 32글자
        // "order_"(6) + 24 = 30글자 → 여유 있게 40 이하 유지
        return "order_" + uuid.substring(0, 24);
    }

    public PaymentAmounts getPaymentAmounts(Order order) {
        BigDecimal itemsTotal = order.getTotalPrice();
        if (itemsTotal == null) {
            itemsTotal = BigDecimal.ZERO;
        }

        // 장바구니가 비어 있으면 모두 0 처리
        if (itemsTotal.compareTo(BigDecimal.ZERO) == 0) {
            return new PaymentAmounts(BigDecimal.ZERO, 0);
        }

        // 배송비 정책: 5만원 초과면 무료, 아니면 2500원
        int deliveryFee =
                itemsTotal.compareTo(BigDecimal.valueOf(50000)) > 0
                        ? 0
                        : 2500;

        return new PaymentAmounts(itemsTotal, deliveryFee);
    }

    private BigDecimal calculateExpectedPayAmount(Order order) {
//        // 1) 상품 합계
//        BigDecimal itemsTotal = order.getTotalPrice();
//
//        // 장바구니가 비어 있으면 0원
//        if (itemsTotal.compareTo(BigDecimal.ZERO) == 0) {
//            return BigDecimal.ZERO;
//        }
//
//        // 2) 배송비 계산 (컨트롤러/HTML에서 쓰던 로직과 맞춰야 함)
//        BigDecimal deliveryFee =
//                itemsTotal.compareTo(BigDecimal.valueOf(50000)) > 0
//                        ? BigDecimal.ZERO
//                        : BigDecimal.valueOf(2500);
//
//        // 3) 상품 + 배송비
//        return itemsTotal.add(deliveryFee);
//    }

        return getPaymentAmounts(order).getTotalPay();
    }

}

