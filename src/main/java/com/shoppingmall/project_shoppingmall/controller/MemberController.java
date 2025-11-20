package com.shoppingmall.project_shoppingmall.controller;

import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import com.shoppingmall.project_shoppingmall.logging.BusinessEventLogger;
import com.shoppingmall.project_shoppingmall.service.*;
import lombok.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final BusinessEventLogger businessEventLogger;

    @GetMapping(value = "/new")
    public String memberForm(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping(value = "/new")
    public String newMember(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model){
        System.out.println("=====회원가입 시작======");
        if(bindingResult.hasErrors()){
            System.out.println("====에러발생 ======");
            return "member/memberForm";
        }

        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            Member saved = memberService.saveMember(member);

            // 회원가입 로그
            businessEventLogger.logUserSignup(saved.getId(), "form");

        } catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }

        return "redirect:/";
    }



    @GetMapping(value = "/login")
    public String loginMember(){
    return "member/new_memberLoginForm";
}


    @GetMapping(value = "/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "member/new_memberLoginForm";
    }

    // [추가] AJAX 이메일 중복 체크 엔드포인트
    @PostMapping("/check-email")
    @ResponseBody
    public Map<String, Boolean> checkEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean exists = memberService.existsByEmail(email); // [추가] 서비스 메서드 호출

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return response;
    }

}
