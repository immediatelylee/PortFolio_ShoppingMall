package com.shoppingmall.project_shoppingmall.dto;

import com.shoppingmall.project_shoppingmall.domain.*;
import org.modelmapper.*;

public class ItemThumbnailDto {
    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    private String repImgYn;

    public static ItemThumbnailDto of(ItemThumbnail itemThumbnail) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(itemThumbnail, ItemThumbnailDto.class);
    }
}
