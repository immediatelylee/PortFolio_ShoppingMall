package com.shoppingmall.project_shoppingmall.config;

import com.shoppingmall.project_shoppingmall.handler.LoginFailureHandler;
import com.shoppingmall.project_shoppingmall.handler.LoginSuccessHandler;
import com.shoppingmall.project_shoppingmall.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    private final MemberService memberService;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;

    // === AuthenticationProvider: UsernameNotFound vs BadCredentials 구분을 위해 꼭 필요 ===
    @Bean
    public DaoAuthenticationProvider authProvider(PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(memberService);
        provider.setPasswordEncoder(encoder);
        // 중요: UsernameNotFoundException을 숨기지 않도록
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/members/login")
//                .defaultSuccessUrl("/")
                .usernameParameter("email")
                .successHandler(loginSuccessHandler)   // v 성공시 로그 찍기
                .failureHandler(loginFailureHandler)   // v 실패시 로그 찍기
//                .failureUrl("/members/login/error")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                .logoutSuccessUrl("/")
        ;

        http.authorizeRequests()
                .mvcMatchers("/", "/members/**", "/item/**", "/img/**","/images/**","/web/**","/error","/notification","/swagger-ui/**", "/v3/api-docs/**","/api/categories/**","/api/search/**","/test/**").permitAll()
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                .mvcMatchers("/wishlist/**").authenticated() // 위시리스트 경로 보호 - 비회원 위시리스트 클릭시 로그인창 이동
                .anyRequest().authenticated()
        ;


        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
        ;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager에 우리가 만든 provider를 등록
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider(passwordEncoder()));
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(memberService)
//                .passwordEncoder(passwordEncoder());
//    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**","/images/**","/web/**","/error");
    }

}
