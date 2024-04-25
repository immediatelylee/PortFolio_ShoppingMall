package com.shoppingmall.project_shoppingmall.dto;

import com.shoppingmall.project_shoppingmall.domain.*;
import lombok.*;
import org.modelmapper.*;

import javax.validation.constraints.*;
import java.util.*;

@Getter @Setter
public class BrandFormDto extends BaseEntity{

    private Long id;

    @NotBlank(message = "브랜드명은 필수 입력 값입니다.")
    private String brandNm;

    private boolean brandStatus;

    private String brandCode;

    public Brand toBrand(){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(this, Brand.class);
    }
    public static BrandFormDto of(Brand brand){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(brand,BrandFormDto.class);
    }
}
