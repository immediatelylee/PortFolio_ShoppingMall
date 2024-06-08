package com.shoppingmall.project_shoppingmall.service;

import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import com.shoppingmall.project_shoppingmall.repository.*;
import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.multipart.*;

import javax.persistence.*;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public Long saveBrand(BrandFormDto brandFormDto) throws Exception {
        String brandCode = generateBrandCode(); // 1. 브랜드 코드 생성
        //브랜드 등록
        Brand brand = brandFormDto.toBrand();
        brand.setBrandCode(brandCode); // 2. 생성된 코드 설정
        brandRepository.save(brand);

        return brand.getId();
    }

    @Transactional(readOnly = true)
    public Page<BrandFormDto> searchBrands(BrandSearchType brandSearchType,String searchValue,String searchStatus ,Pageable pageable) {
        if (searchValue == null || searchValue.isBlank()) {

            return brandRepository.findAll(pageable).map(BrandFormDto::of);
        }

        switch (brandSearchType) {
            case BRAND_NM:
                if (searchStatus.equals("A")){
                    return brandRepository.findByBrandNmContaining(searchValue, pageable).map(BrandFormDto::of);
                }
                else if (searchStatus.equals("T")){
                    return brandRepository.findByBrandNmContainingAndBrandStatusIsTrue(searchValue, pageable).map(BrandFormDto::of);
                } else {
                    return brandRepository.findByBrandNmContainingAndBrandStatusIsFalse(searchValue, pageable).map(BrandFormDto::of);
                }

            case BRAND_CODE:
                if (searchStatus.equals("A")) {
                    return brandRepository.findByBrandCodeContaining(searchValue, pageable).map(BrandFormDto::of);
                }
                else if (searchStatus.equals("T")){
                    return brandRepository.findByBrandCodeContainingAndBrandStatusIsTrue(searchValue, pageable).map(BrandFormDto::of);
                } else {
                    return brandRepository.findByBrandCodeContainingAndBrandStatusIsFalse(searchValue, pageable).map(BrandFormDto::of);
                }

            default:

                // 예상치 못한 brandSearchType 처리 (옵션)
                // 예외를 던지거나, 경고를 기록하거나, 빈 페이지를 반환할 수 있습니다.
                throw new IllegalArgumentException("Unknown BrandSearchType: " + brandSearchType);
        }

    }

    public BrandFormDto getBrand(Long brandId){
        return brandRepository.findById(brandId)
                .map(BrandFormDto::of)
                .orElseThrow(() -> new EntityNotFoundException("브랜드가 없습니다 - brandId: " + brandId));

    }

    public Long updateBrand(BrandFormDto brandFormDto){
        Brand brand = brandRepository.findById(brandFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);

        String brandNm = brandFormDto.getBrandNm();
        Boolean brandStatus = brandFormDto.isBrandStatus();

        brand.setBrandNm(brandNm);
        brand.setBrandStatus(brandStatus);

        return brand.getId();
    }


    public void deleteBrands(List<Long> brandIds) {

        List<Brand> brands = brandRepository.findAllById(brandIds);

        brandRepository.deleteAll(brands);

    }

    public Page<Brand> findAll(Pageable pageable){
        try {
            return brandRepository.findAll(pageable);
        } catch (Exception e) {
            // 예외 처리 코드
            throw new RuntimeException(e);
        }
    }
    public List<Brand> findAll() {
        try {
            return brandRepository.findAll();
        } catch (Exception e){
            // 예외 처리 코드
            throw new RuntimeException(e);
        }
    }

    private String generateBrandCode() {
        // 2. 브랜드 코드 생성 로직
        //B + 7자리 숫자
        return "B" + String.format("%07d", brandRepository.count() + 1);
    }


}
