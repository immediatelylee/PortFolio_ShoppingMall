package com.shoppingmall.project_shoppingmall.dto;

import lombok.*;

import java.time.*;

@Getter
@Setter
public class ItemDto {

    private Long id;
    private String itemNm;
    private Integer price;
    private String itemDetail;
    private String sellStatCd;
    private LocalDateTime regTime;
    private LocalDateTime updateTime;


}
