package com.shoppingmall.project_shoppingmall.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


/**
 * 웹 로그 수집 필터 (개선 버전)
 *
 * 개선 사항:
 * 1. OncePerRequestFilter 사용 → 중복 호출 방지
 * 2. MDC 기반 필드 주입 → Kibana에서 최상위 필드로 검색 가능
 * 3. TraceId 추가 → 전체 요청 추적 가능
 * 4. ResponseWrapper 제거 → 메모리 효율성 향상 (바디는 로깅 안 함)
 * 5. Spring Security 통합 → 표준 방식으로 인증 정보 수집
 * 6. 화이트리스트 기반 파라미터 수집 → 보안 강화
 *
 * 위치: src/main/java/com/example/shop/filter/WebLogFilter.java
 */
@Slf4j
@Component
@Order(2)
public class WebLogFilter extends OncePerRequestFilter {

    // === 기본 정책(Referer/User-Agent 공통) ===
    private static final String DEFAULT_REFERRER = "direct";   // referer 없을 때 값
    private static final int MAX_REFERRER_LENGTH = 512;        // 너무 긴 값 절단
    private static final int MAX_UA_LENGTH = 512;              // UA도 과도하면 절단


    // 로깅에서 제외할 URI 패턴
    private static final Set<String> EXCLUDE_PATTERNS = Set.of(
            "/css/", "/js/", "/images/", "/favicon.ico","/web/","/img/",
            "/webjars/", "/static/", "/actuator/health"
    );

    // 로깅 허용 파라미터 화이트리스트 (보안 강화)
    private static final Set<String> ALLOWED_PARAMS = Set.of(
            "page", "size", "sort", "keyword", "category",
            "productId", "orderId", "quantity", "color"
    );

    // 절대 로깅 금지 필드 (블랙리스트)
    private static final Set<String> FORBIDDEN_PARAMS = Set.of(
            "password", "pwd", "passwd", "cardNumber", "card",
            "cvv", "ssn", "social", "account", "pin"
    );

    /**
     * 필터링 제외 여부 판단
     * OncePerRequestFilter의 shouldNotFilter를 오버라이드하여 정리
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return EXCLUDE_PATTERNS.stream().anyMatch(uri::contains);
    }

    /**
     * 메인 필터 로직
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {


        // 1. TraceIdFilter가 앞에서 넣어준 trace_id가 있는지 먼저 본다
        String traceId = MDC.get("trace_id");

        // 1-1. 없을 때만 새로 생성
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
            MDC.put("trace_id", traceId);
        }

        // 2. 시작 시간 기록
        long startTime = System.currentTimeMillis();

        try {
            // 3. 요청 정보를 MDC에 설정
            setRequestInfoToMDC(request);

            // 4. 실제 컨트롤러 실행
            filterChain.doFilter(request, response);

        } finally {
            // 5. 응답 정보를 MDC에 설정
            long duration = System.currentTimeMillis() - startTime;
            setResponseInfoToMDC(response, duration);

            // 6. 로그 기록 (MDC의 모든 필드가 JSON 최상위로 들어감)
            log.info("HTTP_REQUEST_COMPLETED");

            // 7. MDC 정리 (메모리 누수 방지 - 중요!)
            MDC.clear();
        }
    }

    /**
     * 요청 정보를 MDC에 설정
     * MDC에 넣은 필드들은 logback-spring.xml의 includeMdcKeyName 설정으로
     * Kibana에서 최상위 필드로 검색 가능
     */
    private void setRequestInfoToMDC(HttpServletRequest request) {
        // === 기본 HTTP 정보 ===
        MDC.put("method", request.getMethod());
        MDC.put("uri", request.getRequestURI());

        String queryString = safeQueryString(request);
        if (queryString != null && !queryString.isEmpty()) {
            MDC.put("query_string", queryString);
        }

        // === 클라이언트 정보 ===
        MDC.put("remote_ip", getClientIP(request));

        String userAgent = safeUserAgent(request);
        if (userAgent != null) {
            MDC.put("user_agent", userAgent);
        }

        String referer = safeReferer(request);
        if (referer != null) {
            MDC.put("referer", referer);
        }

        // === 사용자 인증 정보 (Spring Security 사용 시) ===
        setAuthenticationInfo();

        // === 세션 정보 (Spring Security 미사용 시 대안) ===
        HttpSession session = request.getSession(false);
        if (session != null) {
            MDC.put("session_id", session.getId());

            // Spring Security 미사용 시 세션에서 userId 가져오기
            String existingUserId = MDC.get("user_id");
            if (existingUserId == null || existingUserId.isBlank()) {
                Object userId = session.getAttribute("userId");
                if (userId != null) {
                    MDC.put("user_id", userId.toString());
                    MDC.put("is_authenticated", "true");
                }
            }
        }

        // === 요청 파라미터 (화이트리스트 기반) ===
        setParametersToMDC(request);

        // === 쇼핑몰 특화 정보 ===
        extractShoppingMallInfo(request);
    }

    /**
     * Spring Security 인증 정보 추출 (표준 방식)
     */
    private void setAuthenticationInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getPrincipal())) {

                // 사용자 ID (Principal에서 추출)
                Object principal = authentication.getPrincipal();
                if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                    String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
                    MDC.put("user_id", username);
                } else {
                    MDC.put("user_id", principal.toString());
                }

                MDC.put("is_authenticated", "true");

                // 권한 정보
                if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
                    String roles = authentication.getAuthorities().stream()
                            .map(Object::toString)
                            .findFirst()
                            .orElse("USER");
                    MDC.put("user_role", roles);
                }
            } else {
                MDC.put("is_authenticated", "false");
            }
        } catch (Exception e) {
            // Spring Security 미사용 시 무시
            log.trace("Spring Security not configured", e);
        }
    }

    /**
     * 요청 파라미터를 MDC에 설정 (화이트리스트 기반)
     */
    private void setParametersToMDC(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();

            // 금지 파라미터는 절대 로깅 안 함
            if (FORBIDDEN_PARAMS.contains(key.toLowerCase())) {
                continue;
            }

            // 화이트리스트에 있는 파라미터만 로깅
            if (ALLOWED_PARAMS.contains(key)) {
                String value = values.length == 1 ? values[0] : Arrays.toString(values);
                MDC.put("param_" + key, value);
            }
        }
    }

    /**
     * 응답 정보를 MDC에 설정
     */
    private void setResponseInfoToMDC(HttpServletResponse response, long duration) {
        MDC.put("status", String.valueOf(response.getStatus()));
        MDC.put("duration_ms", String.valueOf(duration));
    }

    /**
     * 쇼핑몰 특화 정보 추출
     */
    private void extractShoppingMallInfo(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // === 페이지 타입 분류 ===
        if (uri.matches(".*/product/(detail/)?\\d+.*")) {
            MDC.put("page_type", "product_detail");
            String productId = extractIdFromUri(uri, "/product/");
            if (productId != null) {
                MDC.put("product_id", productId);
            }
        }
        else if (uri.contains("/product")) {
            MDC.put("page_type", "product_list");
        }
        else if (uri.contains("/category/")) {
            MDC.put("page_type", "category");
            String categoryId = extractIdFromUri(uri, "/category/");
            if (categoryId != null) {
                MDC.put("category_id", categoryId);
            }
        }
        else if (uri.contains("/cart")) {
            MDC.put("page_type", "cart");
        }
        else if (uri.contains("/order/checkout")) {
            MDC.put("page_type", "checkout");
        }
        else if (uri.contains("/order")) {
            MDC.put("page_type", "order");
        }
        else if (uri.contains("/search")) {
            MDC.put("page_type", "search");
            String keyword = request.getParameter("keyword");
            if (keyword != null && !keyword.isEmpty()) {
                MDC.put("search_keyword", keyword);
            }
        }
        else if (uri.equals("/") || uri.equals("/index") || uri.equals("/home")) {
            MDC.put("page_type", "home");
        }
        else if (uri.contains("/mypage")) {
            MDC.put("page_type", "mypage");
        }
        else if (uri.contains("/login")) {
            MDC.put("page_type", "login");
        }
        else if (uri.contains("/signup")) {
            MDC.put("page_type", "signup");
        }

        // === 액션 타입 분류 ===
        if ("POST".equals(method)) {
            if (uri.contains("/cart/add")) {
                MDC.put("action", "add_to_cart");
            }
            else if (uri.contains("/cart/remove")) {
                MDC.put("action", "remove_from_cart");
            }
            else if (uri.contains("/order/create")) {
                MDC.put("action", "create_order");
            }
            else if (uri.contains("/payment")) {
                MDC.put("action", "payment");
            }
            else if (uri.contains("/review")) {
                MDC.put("action", "write_review");
            }
            else if (uri.contains("/login")) {
                MDC.put("action", "login");
            }
            else if (uri.contains("/signup")) {
                MDC.put("action", "signup");
            }
        }
    }

    /**
     * URI에서 ID 추출
     */
    private String extractIdFromUri(String uri, String prefix) {
        int index = uri.indexOf(prefix);
        if (index != -1) {
            String afterPrefix = uri.substring(index + prefix.length());
            // "detail/"이 있으면 스킵
            if (afterPrefix.startsWith("detail/")) {
                afterPrefix = afterPrefix.substring(7);
            }
            // 숫자만 추출
            int i = 0;
            while (i < afterPrefix.length() && Character.isDigit(afterPrefix.charAt(i))) {
                i++;
            }
            if (i > 0) {
                return afterPrefix.substring(0, i);
            }
        }
        return null;
    }

    /**
     * 실제 클라이언트 IP 주소 가져오기
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        // X-Forwarded-For 헤더에 여러 IP가 있을 경우 첫 번째가 실제 클라이언트 IP
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    /** referer 안전 추출  */
    private String safeReferer(HttpServletRequest request) {
        // 헤더명은 대소문자 무시되지만 관례적으로 소문자 사용
        String raw = request.getHeader("referer");
        if (raw == null || raw.isBlank()) return DEFAULT_REFERRER;

        // 로그 주입/개행 제거
        String s = raw.replace("\r", "").replace("\n", "").replace("\t", "");

        // 너무 길면 절단
        if (s.length() > MAX_REFERRER_LENGTH) {
            s = s.substring(0, MAX_REFERRER_LENGTH);
        }
        return s;
    }

    /** user-agent 안전 추출 (선택) */
    private String safeUserAgent(HttpServletRequest request) {
        String raw = request.getHeader("User-Agent");
        if (raw == null || raw.isBlank()) return "";

        String s = raw.replace("\r", "").replace("\n", "").replace("\t", "");
        if (s.length() > MAX_UA_LENGTH) {
            s = s.substring(0, MAX_UA_LENGTH);
        }
        return s;
    }

    /** 쿼리스트링도 과도하면 절단(선택) */
    private String safeQueryString(HttpServletRequest request) {
        String qs = request.getQueryString();
        if (qs == null || qs.isBlank()) return null;
        // 개행 제거
        qs = qs.replace("\r", "").replace("\n", "");
        // 필요하면 길이 제한 (예: 2000)
        int max = 2000;
        return qs.length() > max ? qs.substring(0, max) : qs;
    }
}