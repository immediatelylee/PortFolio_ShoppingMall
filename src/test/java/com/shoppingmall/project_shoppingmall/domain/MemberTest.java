package com.shoppingmall.project_shoppingmall.domain;

import com.shoppingmall.project_shoppingmall.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.security.test.context.support.*;
import org.springframework.test.context.*;
import org.springframework.transaction.annotation.*;

import javax.persistence.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
public class MemberTest {
//
//    @Autowired
//    MemberRepository memberRepository;
//
//    @PersistenceContext
//    EntityManager em;
//
//    @Test
//    @DisplayName("Auditing 테스트")
//    @WithMockUser(username = "gildong", roles = "USER")
//    public void auditingTest(){
//        Member newMember = new Member();
//        memberRepository.save(newMember);
//
//        em.flush();
//        em.clear();
//
//        Member member = memberRepository.findById(newMember.getId())
//                .orElseThrow(EntityNotFoundException::new);
//
//        System.out.println("register time : " + member.getRegTime());
//        System.out.println("update time : " + member.getUpdateTime());
//        System.out.println("create member : " + member.getCreatedBy());
//        System.out.println("modify member : " + member.getModifiedBy());
//    }

}
