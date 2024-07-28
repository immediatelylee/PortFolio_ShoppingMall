package com.shoppingmall.project_shoppingmall.domain;

import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import lombok.*;
import org.springframework.security.crypto.password.*;

import javax.persistence.*;

@Entity
@Table(name="member")
@Getter
@Setter
@ToString
public class Member extends BaseEntity{

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;
    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setPhone(memberFormDto.getPhone());
        member.setRole(Role.ADMIN);
        return member;
    }

}
