package com.shoppingmall.project_shoppingmall.handler;

import com.shoppingmall.project_shoppingmall.domain.Member;
import com.shoppingmall.project_shoppingmall.logging.BusinessEventLogger;
import com.shoppingmall.project_shoppingmall.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final BusinessEventLogger businessEventLogger;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String email;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        Long userId = null;
        if (email != null) {
            Member member = memberRepository.findByEmail(email);
            if (member != null) {
                userId = member.getId();
            }
        }

        // * 로그인 성공 로그
        businessEventLogger.logUserLogin(userId, "form_login", true);

        // 원래 가려던 곳으로 보내거나 적당히 응답
        response.sendRedirect("/");
    }
}
