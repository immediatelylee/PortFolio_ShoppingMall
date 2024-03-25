package com.shoppingmall.project_shoppingmall.service;

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
        //브랜드 등록
        Brand brand = brandFormDto.toBrand();
        brandRepository.save(brand);

        return brand.getId();
    }

    public List<Brand> findAll(){
        try {
            return brandRepository.findAll();
        } catch (Exception e) {
            // 예외 처리 코드
            throw new RuntimeException(e);
        }
    }



    public Long updateBrand(BrandFormDto brandFormDto) throws Exception{
        //브랜드 수정
        Brand brand = brandRepository.findById(brandFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        brand.updateBrand(brandFormDto);


        return brand.getId();
    }

//    @Transactional(readOnly = true)
//    public Page<Brand> getAdminBrandPage(ItemSearchDto itemSearchDto, Pageable pageable){
//        return brandRepository.getAdminBrandPage(itemSearchDto, pageable);
//    }

//    @Transactional(readOnly = true)
//    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
//        return itemRepository.getMainItemPage(itemSearchDto, pageable);
//    }



}
