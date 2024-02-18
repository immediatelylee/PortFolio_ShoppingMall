package com.shoppingmall.project_shoppingmall.dto;

import com.shoppingmall.project_shoppingmall.domain.*;
import lombok.*;
import org.modelmapper.*;

import java.util.*;

@Getter @Setter
public class BrandFormDto {

    private Long id;
    private String brandNm;
    private String brandDescription;

    private List<BrandImgDto> brandImgDtoList = new ArrayList<>();
    private List<Long> brandImgIds = new ArrayList<>();

    public Brand toBrand(){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(this, Brand.class);
    }
    public static BrandFormDto of(Brand brand){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(brand,BrandFormDto.class);
    }
}
