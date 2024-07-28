package com.shoppingmall.project_shoppingmall.service;

import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.repository.*;
import lombok.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.security.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;

    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member){
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);

        if(member == null){
            throw new UsernameNotFoundException(email);
        }
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }
    public Member getCurrentMember(Principal principal) {
        String email = principal.getName();
        return memberRepository.findByEmail(email);
    }

    public String getMemberAddressByPrincipal(Principal principal){
        if (principal != null){
            String email = principal.getName();
            Member member = memberRepository.findByEmail(email);
            if (member != null){
                return member.getAddress();
            }

        }
        return null;
    }
}
