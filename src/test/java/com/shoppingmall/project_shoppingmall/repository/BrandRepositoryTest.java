package com.shoppingmall.project_shoppingmall.repository;

import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.data.domain.*;
import org.springframework.test.context.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
public class BrandRepositoryTest {
//
//    @Autowired
//    private BrandRepository brandRepository;
//
//    @Test
//    public void testFindByBrandNmContaining() {
//        String brandNmContaining = "brand1";
//        Pageable pageable = PageRequest.of(0, 10);
//
//        Page<BrandFormDto> brands = brandRepository.findByBrandNmContaining(brandNmContaining, pageable).map(BrandFormDto::of);
//
//        // 결과 검증
//        assertThat(brands.getContent()).hasSize(1);
//        assertThat(brands.getContent().get(0).getBrandNm()).contains(brandNmContaining); // 첫 번째 브랜드 이름에 '테스트' 문자열이 포함되어 있는지 확인
//    }
//
//    @Test
//    public void testFindByBrandCodeContaining() {
//        String brandCodeContaining = "B0000001";
//        Pageable pageable = PageRequest.of(0, 10);
//
//        Page<BrandFormDto> brands = brandRepository.findByBrandCodeContaining(brandCodeContaining, pageable).map(BrandFormDto::of);
//
//        // 결과 검증
//        assertThat(brands).hasSize(1);
//        assertThat(brands.getContent().get(0).getBrandCode()).contains(brandCodeContaining);
//
//    }


}