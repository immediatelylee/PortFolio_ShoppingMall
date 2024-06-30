package com.shoppingmall.project_shoppingmall.dto;

import lombok.*;
import org.w3c.dom.stylesheets.*;

import java.util.*;
@Getter
@Setter
public class IdsTransferDto {
    // 공용부분 (상품-브랜드)
    private List<Long> selectedIds;

    //data-action-type 상품 관리창의 작동유형
    private String dataActionType;

}
