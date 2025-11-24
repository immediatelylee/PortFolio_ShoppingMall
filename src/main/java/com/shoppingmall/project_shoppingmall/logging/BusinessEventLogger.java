package com.shoppingmall.project_shoppingmall.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class BusinessEventLogger {

    private static final Logger businessLog = LoggerFactory.getLogger("BUSINESS_LOG");
    private static final DateTimeFormatter ISO_INSTANT =
            DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    private static final String APP_NAME = "shopping-mall";

    /** 이 로거가 ‘자기가 넣은’ MDC 키만 정리하기 위한 스코프 */
    private static class MdcScope implements AutoCloseable {
        private final List<String> pushedKeys = new ArrayList<>();
        void put(String k, Object v) {
            if (v != null) {
                MDC.put(k, v.toString());
                pushedKeys.add(k);
            }
        }
        @Override public void close() {
            for (String k : pushedKeys) MDC.remove(k);
        }
    }



    /** 공통 베이스 필드: 이 로거에서 넣은 키만 스코프 종료 시 제거됨 */
    private void putBase(MdcScope scope, String eventType) {
        scope.put("timestamp", ISO_INSTANT.format(Instant.now())); // UTC
        scope.put("event_type", eventType);
        scope.put("app", APP_NAME);

        // 필터(요청 경계)에서 trace_id를 이미 넣어두었다는 가정 → 여기선 건드리지 않음.
        // 필요하면 검색 편의상 재기록 가능(주석 해제)
        String traceId = MDC.get("trace_id");
        if (traceId != null && !traceId.isBlank()) {
             scope.put("trace_id", traceId);
        }

        String sessionId = MDC.get("session_id");
        if (sessionId != null && !sessionId.isBlank()) {
            scope.put("session_id", sessionId);
        }
    }

    // ========= 이벤트 메서드들: 모두 try-with-resources 로 통일 =========

    public void logProductView(Long userId, Long productId, String productName, Integer priceInWon) {
        try (MdcScope m = new MdcScope()) {
            putBase(m, "product_view");
            m.put("user_id", userId);
            m.put("product_id", productId);
            m.put("product_name", productName);
            m.put("unit_price", priceInWon);
            businessLog.info("business_event");
        }
    }

    // ==========================
    //     장바구니 관련 이벤트
    // ==========================
    /**  장바구니 페이지 진입 */
    public void logViewCart(Long userId, int itemCount, int cartTotalPrice) {
        try (MdcScope m = new MdcScope()) {
            putBase(m, "view_cart");
            m.put("user_id", userId);
            m.put("cart_item_count", itemCount);       // 장바구니 안 상품 총 개수(수량 합)
            m.put("cart_total_price", cartTotalPrice); // 장바구니 총 금액
            businessLog.info("business_event");
        }
    }

    /**  장바구니 수량 변경 */
    public void logUpdateCartItem(Long userId,
                                  Long productId,
                                  int oldQuantity,
                                  int newQuantity,
                                  int unitPrice) {
        try (MdcScope m = new MdcScope()) {
            putBase(m, "update_cart_item");
            m.put("user_id", userId);
            m.put("product_id", productId);
            m.put("old_quantity", oldQuantity);
            m.put("new_quantity", newQuantity);
            m.put("unit_price", unitPrice);
            m.put("total_price_before", unitPrice * oldQuantity);
            m.put("total_price_after", unitPrice * newQuantity);
            businessLog.info("business_event");
        }
    }

    /**  장바구니에서 결제(주문) 절차 시작 */
    public void logCartCheckoutStart(Long userId,
                                     int itemCount,
                                     int cartTotalPrice) {
        try (MdcScope m = new MdcScope()) {
            putBase(m, "cart_checkout_start");
            m.put("user_id", userId);
            m.put("cart_item_count", itemCount);
            m.put("cart_total_price", cartTotalPrice);
            businessLog.info("business_event");
        }
    }

    /**  게스트 카트 → 회원 카트 병합 (지금은 메서드만, 나중에 사용) */
    public void logCartMerge(Long userId,
                             int anonymousCartItemCount,
                             int userCartItemCountBefore,
                             int mergedItemCount) {
        try (MdcScope m = new MdcScope()) {
            putBase(m, "cart_merge");
            m.put("user_id", userId);
            m.put("anonymous_cart_item_count", anonymousCartItemCount);
            m.put("user_cart_item_count_before", userCartItemCountBefore);
            m.put("merged_item_count", mergedItemCount);
            businessLog.info("business_event");
        }
    }

    public void logAddToCart(Long userId, Long productId, int quantity, Integer unitPriceInWon) {
        try (MdcScope m = new MdcScope()) {
            putBase(m, "add_to_cart");
            m.put("user_id", userId);
            m.put("product_id", productId);
            m.put("quantity", quantity);
            m.put("unit_price", unitPriceInWon);
            if (unitPriceInWon != null) {
                m.put("total_price", unitPriceInWon * quantity);
            }
            businessLog.info("business_event");
        }
    }


    public void logRemoveFromCart(Long userId, Long productId, int quantity, Integer unitPriceInWon) {
        try (MdcScope m = new MdcScope()) {
            putBase(m, "remove_from_cart");
            m.put("user_id", userId);
            m.put("product_id", productId);
            m.put("quantity", quantity);
            m.put("unit_price", unitPriceInWon);
            if (unitPriceInWon != null) {
                m.put("total_price", unitPriceInWon * quantity);
            }
            businessLog.info("business_event");
        }
    }


    public void logOrderCreated(Long userId, Long orderId, Integer totalAmountInWon,
                                int itemCount, String paymentMethod) {
        try (MdcScope m = new MdcScope()) {
            putBase(m, "order_created");
            m.put("user_id", userId);
            m.put("order_id", orderId);
            m.put("amount", totalAmountInWon);
            m.put("item_count", itemCount);
            m.put("payment_method", paymentMethod);
            businessLog.info("business_event");
        }
    }

    public void logPaymentCompleted(Long userId, Long orderId, Integer amountInWon,
                                    String paymentMethod, boolean success) {
        try (MdcScope m = new MdcScope()) {
            putBase(m, "payment_completed");
            m.put("user_id", userId);
            m.put("order_id", orderId);
            m.put("amount", amountInWon);
            m.put("payment_method", paymentMethod);
            m.put("success", success);
            businessLog.info("business_event");
        }
    }

    public void logSearch(Long userId, String keyword, int resultCount) {
        try (MdcScope m = new MdcScope()) {
            putBase(m, "search");
            m.put("user_id", userId);
            m.put("keyword", keyword);
            m.put("result_count", resultCount);
            businessLog.info("business_event");
        }
    }

    public void logCategoryView(Long userId, Long categoryId, String categoryName) {
        try (MdcScope m = new MdcScope()) {
            putBase(m, "category_view");
            m.put("user_id", userId);
            m.put("category_id", categoryId);
            m.put("category_name", categoryName);
            businessLog.info("business_event");
        }
    }

    public void logReviewWritten(Long userId, Long productId, int rating, Long orderId) {
        try (MdcScope m = new MdcScope()) {
            putBase(m, "review_written");
            m.put("user_id", userId);
            m.put("product_id", productId);
            m.put("rating", rating);
            m.put("order_id", orderId);
            businessLog.info("business_event");
        }
    }

    public void logUserLogin(Long userId, String loginMethod, boolean success) {
        try (MdcScope m = new MdcScope()) {
            putBase(m, "user_login");
            m.put("user_id", userId);
            m.put("login_method", loginMethod);
            m.put("success", success);
            businessLog.info("business_event");
        }
    }

    /** 로그인 실패 사유 로그(이메일/사유/클라이언트IP/리퍼러) */
    public void logUserLoginFailed(String emailTried, String reason, String clientIp, String referer) {
        try (MdcScope m = new MdcScope()) {
            putBase(m, "user_login_failed");
            m.put("login_email", emailTried);
            m.put("reason", reason);
            m.put("remote_ip", clientIp);
            m.put("referer", (referer == null || referer.isBlank()) ? "direct" : referer);
            businessLog.info("business_event");
        }
    }

    public void logUserSignup(Long userId, String signupMethod) {
        try (MdcScope m = new MdcScope()) {
            putBase(m, "user_signup");
            m.put("user_id", userId);
            m.put("signup_method", signupMethod);
            businessLog.info("business_event");
        }
    }
}
