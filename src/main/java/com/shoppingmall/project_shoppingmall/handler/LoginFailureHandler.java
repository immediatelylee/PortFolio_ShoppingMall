package com.shoppingmall.project_shoppingmall.handler;

import com.shoppingmall.project_shoppingmall.logging.BusinessEventLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final BusinessEventLogger businessEventLogger;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        String emailTried = request.getParameter("email");
        String reason = mapReason(exception);
        String clientIp = resolveClientIp(request);
        String referer = request.getHeader("Referer");

        // 실패 사유 비즈니스 로그
        businessEventLogger.logUserLoginFailed(emailTried, reason, clientIp, referer);

        response.sendRedirect("/members/login/error");
    }
    private String mapReason(AuthenticationException ex) {
        // 필요시 더 상세 매핑
        String simple = ex.getClass().getSimpleName();
        if (simple.contains("BadCredentials")) return "BadCredentials";
        if (simple.contains("UsernameNotFound")) return "UsernameNotFound";
        if (simple.contains("Locked")) return "AccountLocked";
        if (simple.contains("Disabled")) return "AccountDisabled";
        return simple;
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        return request.getRemoteAddr();
    }


//    private String classify(AuthenticationException ex) {
//        if (ex instanceof BadCredentialsException) return "BadCredentials";
//        if (ex instanceof UsernameNotFoundException) return "UsernameNotFound";
//        if (ex instanceof LockedException) return "Locked";
//        if (ex instanceof DisabledException) return "Disabled";
//        if (ex instanceof AccountExpiredException) return "AccountExpired";
//        if (ex instanceof CredentialsExpiredException) return "CredentialsExpired";
//        // 필요 시 메시지 축약/정규화
//        return ex.getClass().getSimpleName();
//    }
}