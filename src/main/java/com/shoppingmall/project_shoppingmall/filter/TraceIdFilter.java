package com.shoppingmall.project_shoppingmall.filter;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * TraceId 생성 필터
 *
 * 역할: 모든 요청에 고유한 TraceId를 부여하여
 *      컨트롤러/서비스/레포지토리/예외 로그를 연결
 *
 * 실행 순서: 가장 먼저 실행되어야 함 (@Order(1))
 *
 * 사용 예시:
 * [trace_id: abc-123] Controller - 상품 조회 시작
 * [trace_id: abc-123] Service - DB 쿼리 실행
 * [trace_id: abc-123] Error - 상품을 찾을 수 없음
 *
 * → Kibana에서 trace_id로 검색하면 해당 요청의 모든 로그를 볼 수 있음
 *
 * 위치: src/main/java/com/example/shop/filter/TraceIdFilter.java
 */
@Component
@Order(1)  // 가장 먼저 실행
public class TraceIdFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_MDC_KEY = "trace_id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. 클라이언트에서 TraceId를 보냈다면 재사용 (분산 추적)
            String traceId = request.getHeader(TRACE_ID_HEADER);

            // 2. 없다면 새로 생성
            if (traceId == null || traceId.isEmpty()) {
                traceId = generateTraceId();
            }

            // 3. MDC에 설정 (이후 모든 로그에 자동으로 포함됨)
            MDC.put(TRACE_ID_MDC_KEY, traceId);

            // 4. 응답 헤더에도 추가 (클라이언트가 확인 가능)
            response.setHeader(TRACE_ID_HEADER, traceId);

            // 5. 다음 필터로 전달
            filterChain.doFilter(request, response);

        } finally {
            // 6. MDC 정리 (메모리 누수 방지)
            MDC.remove(TRACE_ID_MDC_KEY);
        }
    }

    /**
     * TraceId 생성
     * 형식: 8자리 랜덤 문자열 (UUID의 일부)
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
