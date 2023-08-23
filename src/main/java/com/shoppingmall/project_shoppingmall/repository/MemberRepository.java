package com.shoppingmall.project_shoppingmall.repository;

import com.shoppingmall.project_shoppingmall.domain.*;
import org.springframework.data.jpa.repository.*;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Member findByEmail(String email);
}
