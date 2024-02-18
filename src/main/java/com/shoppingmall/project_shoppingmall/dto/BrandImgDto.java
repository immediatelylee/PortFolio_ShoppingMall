package com.shoppingmall.project_shoppingmall.dto;

import com.shoppingmall.project_shoppingmall.domain.*;
import org.modelmapper.*;

public class BrandImgDto {
    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    public static BrandImgDto of(BrandImg brandImg) {
        ModelMapper modelMapper = new ModelMapper();
        return  modelMapper.map(brandImg,BrandImgDto.class);
    }
}
