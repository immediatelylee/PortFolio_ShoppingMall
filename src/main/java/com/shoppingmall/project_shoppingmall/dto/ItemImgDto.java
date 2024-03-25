package com.shoppingmall.project_shoppingmall.dto;

import com.shoppingmall.project_shoppingmall.domain.*;
import lombok.*;
import org.modelmapper.*;

@Getter
@Setter
public class ItemImgDto {

    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    private String repImgYn;

    public static ItemImgDto of(ItemImg itemImg) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(itemImg,ItemImgDto.class);
    }

}
