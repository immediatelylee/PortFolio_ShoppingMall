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
        int totalAmount = order.getTotalPrice().intValue(); // BigDecimal → int (원 단위)

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
        int totalAmount = order.getTotalPrice().intValue();

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

        log.info(">>> [DTO COMPLETE] start");

        // 1) DB에서 Order 조회 (클라이언트가 준 orderUid 사용)
        Order order = orderRepository.findByOrderUid(dto.getOrderUid())
                .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다."));

        // 2) Iamport 토큰 발급
        String accessToken = iamportClientService.getAccessToken();

        // 3) imp_uid로 결제 정보 조회
        Map<String, Object> paymentData = iamportClientService.getPaymentData(dto.getImpUid(), accessToken);

        Integer amountFromPg = (Integer) paymentData.get("amount");
        String status = (String) paymentData.get("status");
        String merchantUidFromPg = (String) paymentData.get("merchant_uid");

        // 🔍 4) 디버깅용 로그 (한 번 찍어보면 바로 차이 보임)
        log.info(">>> [PG] merchant_uid   = {}", merchantUidFromPg);
        log.info(">>> [DB] order.orderUid = {}", order.getOrderUid());
        log.info(">>> [PG] amount         = {}", amountFromPg);
        log.info(">>> [DB] totalPrice     = {}", order.getTotalPrice());

        // 5) 주문번호 일치 검증: PG vs DB
        if (!merchantUidFromPg.equals(order.getOrderUid())) {
            throw new IllegalStateException("주문번호(merchant_uid)가 일치하지 않습니다.");
        }

        // 6) 결제 금액 검증: PG vs DB (상품 + 배송비 기준)
        BigDecimal pgAmount = BigDecimal.valueOf(amountFromPg.longValue());

        // 서버에서 "기대하는 결제 금액" (상품합 + 배송비)
        BigDecimal expectedAmount = calculateExpectedPayAmount(order);

        log.info(">>> [CHECK] expectedAmount = {}", expectedAmount);
        log.info(">>> [CHECK] pgAmount       = {}", pgAmount);

        if (expectedAmount.compareTo(pgAmount) != 0) {
            throw new IllegalStateException("결제 금액이 일치하지 않습니다.");
        }

        // 7) 결제 상태 검증
        if (!"paid".equals(status)) {
            throw new IllegalStateException("결제 상태가 완료(paid)가 아닙니다. 상태=" + status);
        }

        // 8) 여기까지 통과하면 정상 결제 → 주문/결제 상태 확정
        order.setOrderStatus(OrderStatus.PAID);

        OrderPayment payment = order.getOrderPayment();
        if (payment == null) {
            payment = new OrderPayment();
            payment.setOrder(order);
        }

        payment.setPaymentMethod("CARD"); // TODO: rsp.pay_method를 DTO로 받아서 세팅 가능
        payment.setAmount(order.getTotalPrice());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        orderPaymentRepository.save(payment);
        orderRepository.save(order);

        // 🔹 비즈니스 로그: 결제 완료
        int totalAmountInWon = order.getTotalPrice().intValue();

        businessEventLogger.logPaymentCompleted(
                order.getOrderUid(),               // orderUid
                order.getMember().getId(),         // userId
                totalAmountInWon,                  // totalAmountInWon
                true,                              // success
                payment.getPaymentMethod(),        // paymentMethod
                payment.getPaymentStatus().name(), // paymentStatus
                order.getOrderStatus().name(),     // orderStatus
                payment.getId(),                   // paymentId
                null                               // cancelReason (성공이라 없음)
        );
    }


    private String generateOrderUid() {
        String uuid = UUID.randomUUID().toString().replace("-", ""); // 32글자
        // "order_"(6) + 24 = 30글자 → 여유 있게 40 이하 유지
        return "order_" + uuid.substring(0, 24);
    }

    private BigDecimal calculateExpectedPayAmount(Order order) {
        // 1) 상품 합계
        BigDecimal itemsTotal = order.getTotalPrice();

        // 장바구니가 비어 있으면 0원
        if (itemsTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        // 2) 배송비 계산 (컨트롤러/HTML에서 쓰던 로직과 맞춰야 함)
        BigDecimal deliveryFee =
                itemsTotal.compareTo(BigDecimal.valueOf(50000)) > 0
                        ? BigDecimal.ZERO
                        : BigDecimal.valueOf(2500);

        // 3) 상품 + 배송비
        return itemsTotal.add(deliveryFee);
    }


}

